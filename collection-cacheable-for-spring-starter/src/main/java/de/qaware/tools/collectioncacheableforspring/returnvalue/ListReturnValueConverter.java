/*-
 * #%L
 * Collection Cacheable for Spring :: Starter
 * %%
 * Copyright (C) 2020 - 2022 QAware GmbH
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

package de.qaware.tools.collectioncacheableforspring.returnvalue;

import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Return value converter for {@link List lists}
 * containing items implementing {@link HasCacheKey}.
 */
public class ListReturnValueConverter implements ReturnValueConverter {

    public static final int ORDER = 100;

    @Override
    public boolean canHandle(Class<?> returnType) {
        return returnType.isAssignableFrom(List.class);
    }

    @Override
    public MapLikeReturnValue convert(Object invocationResult) {
        if (invocationResult instanceof List) {
            return new MapLikeArrayList((List<?>) invocationResult);
        }
        throw new IllegalStateException("Expecting invocation result to implement List, but is " + invocationResult.getClass());
    }

    @Override
    public MapLikeReturnValue convert(@Nullable Object invocationResult, Map<Object, Object> cacheResult) {
        if (invocationResult instanceof List) {
            return Stream.concat(((List<?>) invocationResult).stream(), cacheResult.values().stream())
                    .collect(Collectors.toCollection(MapLikeArrayList::new));
        } else {
            return new MapLikeArrayList(cacheResult.values());
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    private static class MapLikeArrayList extends ArrayList<Object> implements MapLikeReturnValue {

        public MapLikeArrayList() {
            super();
        }

        public MapLikeArrayList(Collection<?> c) {
            super(c);
        }

        @Override
        public void forEach(BiConsumer<Object, Object> action) {
            forEach(item -> action.accept(extractCacheKey(item), item));
        }

        @Override
        public boolean containsKey(Object key) {
            return stream().anyMatch(item -> extractCacheKey(item).equals(key));
        }

        private Object extractCacheKey(Object item) {
            if (item instanceof HasCacheKey) {
                return ((HasCacheKey) item).getCacheKey();
            }
            throw new IllegalStateException("Expecting item " + item + " to implement " + HasCacheKey.class);
        }
    }

}
