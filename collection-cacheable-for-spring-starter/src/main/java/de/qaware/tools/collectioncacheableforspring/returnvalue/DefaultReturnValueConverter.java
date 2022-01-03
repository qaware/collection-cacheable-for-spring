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

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Default return value converter for {@link Map}.
 */
public class DefaultReturnValueConverter implements ReturnValueConverter {

    public static final int ORDER = 100;

    @Override
    public boolean canHandle(Class<?> returnType) {
        return returnType.isAssignableFrom(Map.class);
    }

    @Override
    public MapLikeReturnValue convert(Object invocationResult) {
        return (MapLikeReturnValue) Proxy.newProxyInstance(
                MapLikeReturnValue.class.getClassLoader(),
                new Class<?>[]{Map.class, MapLikeReturnValue.class},
                (proxy, method, args) -> method.invoke(invocationResult, args)
        );
    }

    @Override
    public MapLikeReturnValue convert(@Nullable Object invocationResult, Map<Object, Object> cacheResult) {
        if (invocationResult instanceof Map) {
            cacheResult.putAll((Map<?, ?>) invocationResult);
        }
        return (MapLikeReturnValue) Proxy.newProxyInstance(
                MapLikeReturnValue.class.getClassLoader(),
                new Class<?>[]{Map.class, MapLikeReturnValue.class},
                (proxy, method, args) -> method.invoke(cacheResult, args)
        );
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
