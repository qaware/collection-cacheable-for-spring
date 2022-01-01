/*
 * #%L
 * Collection Cacheable for Spring :: Starter
 * %%
 * Copyright (C) 2020 QAware GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.qaware.tools.collectioncacheableforspring.creator;

import org.springframework.core.Ordered;

import java.util.Collection;

/**
 * Collection creator used when a sub-set of the given arguments (IDs)
 * needs to be constructed.
 */
public interface CollectionCreator extends Ordered {

    /**
     * Check if given type class can be handled.
     *
     * @param cls class type to be handled
     * @return true if type can be handled, false otherwise
     */
    boolean canHandle(Class<?> cls);

    /**
     * Create a new modifiable collection with type as given to {@link #canHandle} and content given.
     *
     * @param collection initial content of created collection
     * @param <T>        type of items
     * @return created collection
     */
    <T> Collection<T> create(Collection<T> collection);
}
