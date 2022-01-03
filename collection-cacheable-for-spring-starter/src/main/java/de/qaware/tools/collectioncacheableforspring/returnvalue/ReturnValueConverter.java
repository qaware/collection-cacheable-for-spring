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

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public interface ReturnValueConverter extends Ordered {

    /**
     * Check if given type of return value can be handled.
     *
     * @param returnType class type to be handled
     * @return true if type can be handled, false otherwise
     */
    boolean canHandle(Class<?> returnType);

    MapLikeReturnValue convert(Object invocationResult);

    MapLikeReturnValue convert(@Nullable Object invocationResult, Map<Object, Object> cacheResult);

    interface MapLikeReturnValue {
        /**
         * Matches {@link Map#forEach(BiConsumer)}.
         *
         * @param action action to perform
         */
        void forEach(BiConsumer<Object, Object> action);

        /**
         * Matches {@link Map#containsKey(Object)}
         *
         * @param key key
         * @return true if key is contained, false otherwise
         */
        boolean containsKey(Object key);
    }
}
