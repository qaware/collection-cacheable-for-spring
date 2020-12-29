package de.qaware.collectioncacheableforspring;

import java.util.Objects;
import java.util.StringJoiner;

public class CollectionCacheableTestId {
    private final String id;

    public CollectionCacheableTestId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionCacheableTestId myKey = (CollectionCacheableTestId) o;
        return Objects.equals(id, myKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CollectionCacheableTestId.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .toString();
    }
}
