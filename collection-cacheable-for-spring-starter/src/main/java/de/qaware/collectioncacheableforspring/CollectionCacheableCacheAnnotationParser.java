/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qaware.collectioncacheableforspring;

import org.springframework.cache.annotation.CacheAnnotationParser;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Strategy implementation for parsing Spring's {@link Caching}, {@link Cacheable},
 * {@link CacheEvict}, and {@link CachePut} annotations.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 3.1
 */
@SuppressWarnings("serial")
public class CollectionCacheableCacheAnnotationParser implements CacheAnnotationParser, Serializable {

    private static final String MESSAGE_INVALID_COLLECTION_CACHEABLE_ANNOTATION_CONFIGURATION =
            "Invalid CollectionCacheable annotation configuration on '%s'.";

    private static final String MESSAGE_INVALID_CACHE_ANNOTATION_CONFIGURATION =
            "Invalid cache annotation configuration on '%s'.";

    @Override
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        // @CollectionCacheable only makes sense on methods
        return Collections.emptyList();
    }

    @Override
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(method.getDeclaringClass());
        return parseCacheAnnotations(defaultConfig, method);
    }

    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, Method method) {
        Collection<CacheOperation> ops = parseCacheAnnotations(cachingConfig, method, false);
        if (ops != null && ops.size() > 1) {
            // More than one operation found -> local declarations override interface-declared ones...
            Collection<CacheOperation> localOps = parseCacheAnnotations(cachingConfig, method, true);
            if (localOps != null) {
                return localOps;
            }
        }
        return ops;
    }

    private Collection<CacheOperation> parseCacheAnnotations(
            DefaultCacheConfig cachingConfig, Method method, boolean localOnly) {

        Collection<? extends Annotation> anns = (localOnly ?
                AnnotatedElementUtils.getAllMergedAnnotations(method, CollectionCacheable.class) :
                AnnotatedElementUtils.findAllMergedAnnotations(method, CollectionCacheable.class));
        if (anns.isEmpty()) {
            return Collections.emptyList();
        }

        final Collection<CacheOperation> ops = new ArrayList<>(1);
        anns.stream().filter(ann -> ann instanceof CollectionCacheable).forEach(
                ann -> ops.add(parseCollectionCacheableAnnotation(method, cachingConfig, (CollectionCacheable) ann)));
        return ops;
    }

    private CollectionCacheableOperation parseCollectionCacheableAnnotation(
            Method method, DefaultCacheConfig defaultConfig, CollectionCacheable collectionCacheable) {

        boolean isFindAll = checkFindAll(method);
        validateMethodSignature(isFindAll, method);

        CollectionCacheableOperation.Builder builder = new CollectionCacheableOperation.Builder();

        builder.setName(method.toString());
        builder.setCacheNames(collectionCacheable.cacheNames());
        builder.setCondition(collectionCacheable.condition());
        builder.setKey(collectionCacheable.key());
        builder.setKeyGenerator(collectionCacheable.keyGenerator());
        builder.setCacheManager(collectionCacheable.cacheManager());
        builder.setCacheResolver(collectionCacheable.cacheResolver());
        builder.setUnless(collectionCacheable.unless());
        builder.setFindAll(isFindAll);
        builder.setPutNull(collectionCacheable.putNull());

        defaultConfig.applyDefault(builder);
        CollectionCacheableOperation op = builder.build();
        validateCollectionCacheableOperation(method, op);

        return op;
    }

    private boolean checkFindAll(Method method) {
        return method.getParameterTypes().length == 0;
    }

    private static void validateMethodSignature(boolean isFindAll, Method method) {
        validateMethodReturnType(method);
        if (isFindAll) {
            return;
        }
        validateMethodArguments(method);
        validateGenericMethodSignature(method);
    }

    private static void validateMethodReturnType(Method method) {
        if (!method.getReturnType().isAssignableFrom(Map.class)) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_COLLECTION_CACHEABLE_ANNOTATION_CONFIGURATION +
                            " Method return type is not assignable from Map.",
                    method.toString()));
        }
    }

    private static void validateMethodArguments(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1 || !parameterTypes[0].equals(Collection.class)) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_COLLECTION_CACHEABLE_ANNOTATION_CONFIGURATION +
                            " Did not find zero or one Collection argument.",
                    method.toString()));
        }
    }

    private static void validateGenericMethodSignature(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (genericParameterTypes.length != 1 || !(genericParameterTypes[0] instanceof ParameterizedType)) {
            // assume method is not generic
            return;
        }
        if (!(method.getGenericReturnType() instanceof ParameterizedType)) {
            // assume method is not generic
            return;
        }
        ParameterizedType parameterizedCollection = (ParameterizedType) genericParameterTypes[0];
        if (parameterizedCollection.getActualTypeArguments().length != 1) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_COLLECTION_CACHEABLE_ANNOTATION_CONFIGURATION +
                            " Parameterized collection does not have exactly one type argument.",
                    method.toString()));
        }
        ParameterizedType parameterizedMap = (ParameterizedType) method.getGenericReturnType();
        if (parameterizedMap.getActualTypeArguments().length != 2) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_COLLECTION_CACHEABLE_ANNOTATION_CONFIGURATION +
                            " Parameterized map does not have exactly two type arguments.",
                    method.toString()));
        }
        if (!parameterizedMap.getActualTypeArguments()[0].equals(parameterizedCollection.getActualTypeArguments()[0])) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_COLLECTION_CACHEABLE_ANNOTATION_CONFIGURATION +
                            " The Map key type should be equal to the collection type.",
                    method.toString()));
        }
    }

    private void validateCollectionCacheableOperation(AnnotatedElement ae, CollectionCacheableOperation operation) {
        if (StringUtils.hasText(operation.getCacheManager()) && StringUtils.hasText(operation.getCacheResolver())) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_CACHE_ANNOTATION_CONFIGURATION +
                            " Both 'cacheManager' and 'cacheResolver' attributes have been set." +
                            " These attributes are mutually exclusive: the cache manager is used to configure a default cache resolver if none is set." +
                            " If a cache resolver is set, the cache manager won't be used.",
                    ae.toString()));
        }
        if (operation.isFindAll() && StringUtils.hasText(operation.getCondition())) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_CACHE_ANNOTATION_CONFIGURATION +
                            " Cannot use 'condition' on 'findAll'-like methods.",
                    ae.toString()));
        }
        if (operation.isFindAll() && operation.isPutNull()) {
            throw new IllegalStateException(String.format(
                    MESSAGE_INVALID_CACHE_ANNOTATION_CONFIGURATION +
                            " 'putNull' has no effect on 'findAll'-like methods.",
                    ae.toString()));
        }
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || other instanceof CollectionCacheableCacheAnnotationParser);
    }

    @Override
    public int hashCode() {
        return CollectionCacheableCacheAnnotationParser.class.hashCode();
    }

    /**
     * Provides default settings for a given set of cache operations.
     */
    private static class DefaultCacheConfig {

        private final Class<?> target;

        @Nullable
        private String[] cacheNames;

        @Nullable
        private String keyGenerator;

        @Nullable
        private String cacheManager;

        @Nullable
        private String cacheResolver;

        private boolean initialized = false;

        public DefaultCacheConfig(Class<?> target) {
            this.target = target;
        }

        /**
         * Apply the defaults to the specified {@link CacheOperation.Builder}.
         *
         * @param builder the operation builder to update
         */
        public void applyDefault(CacheOperation.Builder builder) {
            initialize();
            if (builder.getCacheNames().isEmpty() && this.cacheNames != null) {
                builder.setCacheNames(this.cacheNames);
            }
            if (!StringUtils.hasText(builder.getKey()) && !StringUtils.hasText(builder.getKeyGenerator()) &&
                    StringUtils.hasText(this.keyGenerator)) {
                builder.setKeyGenerator(this.keyGenerator);
            }
            if (StringUtils.hasText(builder.getCacheManager()) || StringUtils.hasText(builder.getCacheResolver())) {
                // One of these is set so we should not inherit anything
            } else if (StringUtils.hasText(this.cacheResolver)) {
                builder.setCacheResolver(this.cacheResolver);
            } else if (StringUtils.hasText(this.cacheManager)) {
                builder.setCacheManager(this.cacheManager);
            }
        }

        private void initialize() {
            if (this.initialized) {
                return;
            }
            CacheConfig annotation = AnnotatedElementUtils.findMergedAnnotation(this.target, CacheConfig.class);
            if (annotation != null) {
                this.cacheNames = annotation.cacheNames();
                this.keyGenerator = annotation.keyGenerator();
                this.cacheManager = annotation.cacheManager();
                this.cacheResolver = annotation.cacheResolver();
            }
            this.initialized = true;
        }
    }
}
