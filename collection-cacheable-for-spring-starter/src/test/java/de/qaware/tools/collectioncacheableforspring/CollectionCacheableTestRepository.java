package de.qaware.tools.collectioncacheableforspring;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@CacheConfig(cacheNames = CollectionCacheableTestRepository.CACHE_NAME)
public class CollectionCacheableTestRepository {
    public static final String CACHE_NAME = "myCache";

    private final CollectionCacheableTestDbRepository myDbRepository;

    public CollectionCacheableTestRepository(CollectionCacheableTestDbRepository myDbRepository) {
        this.myDbRepository = myDbRepository;
    }

    @Cacheable
    public CollectionCacheableTestValue findById(CollectionCacheableTestId id) {
        return myDbRepository.findById(id);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id.id")
    public CollectionCacheableTestValue findByIdWithKey(CollectionCacheableTestId id) {
        return myDbRepository.findById(id);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIds(Collection<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsSet(Set<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsList(List<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsArrayList(ArrayList<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsLinkedHashSet(LinkedHashSet<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(condition = "#ids.size() < 3")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithCondition(Collection<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(unless = "#result.size() > 1")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithUnless(Collection<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(cacheNames = CACHE_NAME, key = "#p0.id")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithKey(Collection<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(putNull = true)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithPutNull(Collection<CollectionCacheableTestId> ids) {
        return findByIdsInternal(ids);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAll() {
        return myDbRepository.findAll();
    }

    @CollectionCacheable(cacheNames = CACHE_NAME, unless = "#result.size() > 1")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAllWithUnless() {
        return myDbRepository.findAll();
    }

    @CollectionCacheable(cacheNames = CACHE_NAME, key = "#result.id")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAllWithKey() {
        return myDbRepository.findAll();
    }

    private Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsInternal(Collection<CollectionCacheableTestId> ids) {
        // just a "simulation" of an efficient findByIds call to the underlying persistence layer
        // in real use cases, this should be some efficient query!
        Map<CollectionCacheableTestId, CollectionCacheableTestValue> result = new HashMap<>();
        for (CollectionCacheableTestId id : ids) {
            CollectionCacheableTestValue value = myDbRepository.findById(id);
            // do not explicitly put null values into map
            if (value != null) {
                result.put(id, value);
            }
        }
        return result;
    }
}
