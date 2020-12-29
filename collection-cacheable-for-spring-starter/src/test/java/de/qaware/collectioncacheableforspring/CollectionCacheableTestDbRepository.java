package de.qaware.collectioncacheableforspring;

import java.util.Map;

public interface CollectionCacheableTestDbRepository {
    CollectionCacheableTestValue findById(CollectionCacheableTestId id);

    Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAll();
}
