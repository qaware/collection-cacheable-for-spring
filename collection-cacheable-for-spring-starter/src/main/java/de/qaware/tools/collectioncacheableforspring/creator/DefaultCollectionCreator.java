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

package de.qaware.tools.collectioncacheableforspring.creator;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultCollectionCreator implements CollectionCreator {

    public static final int ORDER = -100;

    @Override
    public boolean canHandle(Class<?> cls) {
        return cls.isAssignableFrom(LinkedList.class);
    }

    @Override
    public <T> Collection<T> create(Collection<T> collection) {
        return new LinkedList<>(collection);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
