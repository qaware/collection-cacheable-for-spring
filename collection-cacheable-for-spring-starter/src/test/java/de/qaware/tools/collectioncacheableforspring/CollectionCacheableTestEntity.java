package de.qaware.tools.collectioncacheableforspring;


import de.qaware.tools.collectioncacheableforspring.returnvalue.HasCacheKey;

import java.util.Objects;
import java.util.StringJoiner;

public class CollectionCacheableTestEntity implements HasCacheKey {
    private final CollectionCacheableTestId id;
    private final CollectionCacheableTestValue value;

    public CollectionCacheableTestEntity(CollectionCacheableTestId id, CollectionCacheableTestValue value) {
        this.id = id;
        this.value = value;
    }

    public CollectionCacheableTestId getId() {
        return id;
    }

    public CollectionCacheableTestValue getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionCacheableTestEntity that = (CollectionCacheableTestEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CollectionCacheableTestEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("value=" + value)
                .toString();
    }

    @Override
    public Object getCacheKey() {
        return id;
    }
}
