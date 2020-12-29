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

import org.springframework.cache.interceptor.CachePutOperation;

public class CollectionCacheableOperation extends CachePutOperation {

    private final boolean isFindAll;

    private final boolean putNull;

    public CollectionCacheableOperation(Builder b) {
        super(b);
        this.isFindAll = b.isFindAll;
        this.putNull = b.putNull;
    }

    public boolean isFindAll() {
        return isFindAll;
    }

    public boolean isPutNull() {
        return putNull;
    }

    public static class Builder extends CachePutOperation.Builder {

        private boolean isFindAll;

        private boolean putNull;

        public void setFindAll(boolean findAll) {
            isFindAll = findAll;
        }

        public void setPutNull(boolean putNull) {
            this.putNull = putNull;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | isFindAll ='");
            sb.append(this.isFindAll);
            sb.append("'");
            sb.append(" | putNull ='");
            sb.append(this.putNull);
            sb.append("'");
            return sb;
        }

        @Override
        public CollectionCacheableOperation build() {
            return new CollectionCacheableOperation(this);
        }
    }
}
