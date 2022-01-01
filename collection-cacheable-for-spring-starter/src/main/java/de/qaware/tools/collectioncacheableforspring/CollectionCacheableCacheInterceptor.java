/*-
 * #%L
 * Collection Cacheable for Spring :: Starter
 * %%
 * Copyright (C) 2020 QAware GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.qaware.tools.collectioncacheableforspring;

import de.qaware.tools.collectioncacheableforspring.creator.CollectionCreator;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionCacheableCacheInterceptor extends CacheInterceptor {

    private static final Object NO_RESULT = new Object();

    @Override
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] invocationArgs) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        CacheOperationSource cacheOperationSource = getCacheOperationSource();
        if (cacheOperationSource != null) {
            Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);

            CollectionCacheableOperation collectionCacheableOperation = findCollectionCacheableOperation(operations);
            if (collectionCacheableOperation != null) {
                return handleCollectionCacheable(collectionCacheableOperation, targetClass, invoker, target, method, invocationArgs);
            }
        }
        return super.execute(invoker, target, method, invocationArgs);
    }

    private Object handleCollectionCacheable(CollectionCacheableOperation operation, Class<?> targetClass, CacheOperationInvoker invoker, Object target, Method method, Object[] invocationArgs) {
        CollectionCacheableOperationContext context = getCollectionCacheableOperationContext(operation, method, target, targetClass);

        if (operation.isFindAll()) {
            return handleIsFindAll(invoker, context);
        }
        CollectionCreator collectionCreator = Objects.requireNonNull(operation.getCollectionCreator(), "collectionCreator must be set for non-isFindAll operations");

        Collection<?> idsArgument = injectCollectionArgument(collectionCreator, invocationArgs);
        if (!context.isConditionPassingWithArgument(idsArgument)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking method as condition is not passing with argument " + idsArgument);
            }
            return invokeMethod(invoker);
        }

        Map<Object, Object> result = new HashMap<>();
        Iterator<?> idIterator = idsArgument.iterator();
        while (idIterator.hasNext()) {
            Object id = idIterator.next();
            Object key = context.generateKeyFromSingleArgument(id);
            Cache.ValueWrapper cacheHit = findInCaches(context, key);
            if (cacheHit != null) {
                if (cacheHit.get() != null) {
                    result.put(id, cacheHit.get());
                } else if (logger.isTraceEnabled()) {
                    logger.trace("Ignoring null cache hit for key '" + key + "'");
                }
                idIterator.remove();
            }
        }
        if (!idsArgument.isEmpty()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking method with remaining ids " + idsArgument);
            }
            Map<?, ?> uncachedResult = invokeMethod(invoker);
            result.putAll(uncachedResult);
            if (context.canPutToCache(uncachedResult)) {
                putUncachedResultToCache(uncachedResult, context);
                if (operation.isPutNull()) {
                    putNullToCache(uncachedResult.keySet(), idsArgument, context);
                }
            }
        }
        return result;
    }

    private Object handleIsFindAll(CacheOperationInvoker invoker, CollectionCacheableOperationContext context) {
        Map<?, ?> uncachedResult = invokeMethod(invoker);
        if (context.canPutToCache(uncachedResult)) {
            logger.trace("Putting result into cache for findAll case");
            putUncachedResultToCache(uncachedResult, context);
        }
        return uncachedResult;
    }

    private void putUncachedResultToCache(Map<?, ?> uncachedResult, CollectionCacheableOperationContext context) {
        for (Map.Entry<?, ?> entry : uncachedResult.entrySet()) {
            Object key = context.generateKeyFromSingleArgument(entry.getKey());
            for (Cache cache : context.getCaches()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Putting value for key '" + key + "' into cache '" + cache.getName() + "'");
                }
                doPut(cache, key, entry.getValue());
            }
        }
    }

    private void putNullToCache(Set<?> resultKeys, Collection<?> idsArgument, CollectionCacheableOperationContext context) {
        for (Object id : idsArgument) {
            if (!resultKeys.contains(id)) {
                Object key = context.generateKeyFromSingleArgument(id);
                for (Cache cache : context.getCaches()) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Putting explicit null for key '" + key + "' into cache '" + cache.getName() + "'");
                    }
                    doPut(cache, key, null);
                }
            }
        }
    }

    private Map<?, ?> invokeMethod(CacheOperationInvoker invoker) {
        Object result = invoker.invoke();
        if (result instanceof Map) {
            return (Map<?, ?>) result;
        }
        throw new IllegalStateException("Expecting result of invocation to be a Map");
    }

    @Nullable
    private Cache.ValueWrapper findInCaches(CollectionCacheableOperationContext context, Object key) {
        for (Cache cache : context.getCaches()) {
            Cache.ValueWrapper wrapper = doGet(cache, key);
            if (wrapper != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Found cache hit for key '" + key + "' from cache '" + cache.getName() + "'");
                }
                return wrapper;
            }
        }
        return null;
    }

    private static Collection<?> injectCollectionArgument(CollectionCreator collectionCreator, Object[] invocationArgs) {
        if (invocationArgs.length == 1 && invocationArgs[0] instanceof Collection) {
            Collection<?> createdCollection = collectionCreator.create((Collection<?>) invocationArgs[0]);
            invocationArgs[0] = createdCollection;
            return createdCollection;
        }
        throw new IllegalStateException("Did not find exactly one Collection-like argument");
    }

    @Nullable
    private CollectionCacheableOperation findCollectionCacheableOperation(@Nullable Collection<CacheOperation> operations) {
        if (operations == null) {
            return null;
        }
        List<CollectionCacheableOperation> collectionCacheableOperations = operations.stream()
                .filter(CollectionCacheableOperation.class::isInstance)
                .map(CollectionCacheableOperation.class::cast)
                .collect(Collectors.toList());
        if (collectionCacheableOperations.isEmpty()) {
            return null;
        }
        if (collectionCacheableOperations.size() == 1) {
            return collectionCacheableOperations.get(0);
        }
        throw new IllegalStateException("Found more than one @CollectionCacheable annotation");
    }

    protected CollectionCacheableOperationContext getCollectionCacheableOperationContext(
            CacheOperation operation, Method method, Object target, Class<?> targetClass) {
        CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
        Object[] currentArgs = new Object[]{null};
        return new CollectionCacheableOperationContext(metadata, operation, currentArgs, target);
    }

    protected class CollectionCacheableOperationContext extends CacheOperationContext {
        private final CacheOperation operation;
        private final Object[] currentArgs;

        public CollectionCacheableOperationContext(CacheOperationMetadata metadata, CacheOperation operation, Object[] currentArgs, Object target) {
            super(metadata, currentArgs, target);
            this.operation = operation;
            this.currentArgs = currentArgs;
        }

        public Object generateKeyFromSingleArgument(Object arg) {
            currentArgs[0] = arg;
            Object key = generateKey(arg);
            if (key == null) {
                throw new IllegalArgumentException("Null key returned for cache operation (maybe you are " +
                        "using named params on classes without debug info?) " + operation);
            }
            return key; // provide arg as result as well for findAll case
        }

        @Override
        protected boolean canPutToCache(Object result) {
            currentArgs[0] = null;
            return super.canPutToCache(result);
        }

        public boolean isConditionPassingWithArgument(Object arg) {
            currentArgs[0] = arg;
            return super.isConditionPassing(NO_RESULT);
        }

        @Override
        public Collection<? extends Cache> getCaches() {
            return super.getCaches();
        }
    }
}
