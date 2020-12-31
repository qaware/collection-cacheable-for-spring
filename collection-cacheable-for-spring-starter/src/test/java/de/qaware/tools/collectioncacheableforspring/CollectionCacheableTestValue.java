package de.qaware.tools.collectioncacheableforspring;

import java.util.Objects;
import java.util.StringJoiner;

public class CollectionCacheableTestValue {
    private final String value;

    public CollectionCacheableTestValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionCacheableTestValue myValue = (CollectionCacheableTestValue) o;
        return Objects.equals(value, myValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CollectionCacheableTestValue.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
