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

import org.springframework.cache.interceptor.CachePutOperation;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CollectionCacheableOperation that = (CollectionCacheableOperation) o;
        return isFindAll == that.isFindAll && putNull == that.putNull;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isFindAll, putNull);
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
