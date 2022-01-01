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
import org.springframework.cache.interceptor.CachePutOperation;

public class CollectionCacheableOperation extends CachePutOperation {

    private final boolean isFindAll;

    private final boolean putNull;

    private final CollectionCreator collectionCreator;

    public CollectionCacheableOperation(Builder b) {
        super(b);
        this.isFindAll = b.isFindAll;
        this.putNull = b.putNull;
        this.collectionCreator = b.collectionCreator;
    }

    public boolean isFindAll() {
        return isFindAll;
    }

    public boolean isPutNull() {
        return putNull;
    }

    public CollectionCreator getCollectionCreator() {
        return collectionCreator;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CollectionCacheableOperation && toString().equals(o.toString()));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static class Builder extends CachePutOperation.Builder {

        private boolean isFindAll;

        private boolean putNull;

        private CollectionCreator collectionCreator;

        public void setFindAll(boolean findAll) {
            isFindAll = findAll;
        }

        public void setPutNull(boolean putNull) {
            this.putNull = putNull;
        }

        public void setCollectionCreator(CollectionCreator collectionCreator) {
            this.collectionCreator = collectionCreator;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription()
                    .append(" | isFindAll=")
                    .append(isFindAll)
                    .append(" | putNull=")
                    .append(putNull);
            if (this.collectionCreator != null) {
                sb
                        .append(" | collectionCreator=")
                        .append(this.collectionCreator.getClass().getSimpleName());
            }
            return sb;
        }

        @Override
        public CollectionCacheableOperation build() {
            return new CollectionCacheableOperation(this);
        }
    }
}
