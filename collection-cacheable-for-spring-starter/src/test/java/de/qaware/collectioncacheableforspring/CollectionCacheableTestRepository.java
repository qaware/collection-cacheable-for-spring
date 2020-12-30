package de.qaware.collectioncacheableforspring;

import org.springframework.cache.annotation.Cacheable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


public class CollectionCacheableTestRepository {
    public static final String CACHE_NAME = "myCache";

    private final CollectionCacheableTestDbRepository myDbRepository;

    public CollectionCacheableTestRepository(CollectionCacheableTestDbRepository myDbRepository) {
        this.myDbRepository = myDbRepository;
    }

    @Cacheable(cacheNames = CACHE_NAME)
    public CollectionCacheableTestValue findById(CollectionCacheableTestId id) {
        return myDbRepository.findById(id);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id.id")
    public CollectionCacheableTestValue findByIdWithKey(CollectionCacheableTestId id) {
        return myDbRepository.findById(id);
    }

    @CollectionCacheable(CACHE_NAME)
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIds(Collection<CollectionCacheableTestId> ids) {
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = CACHE_NAME, condition = "#ids.size() < 3")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithCondition(Collection<CollectionCacheableTestId> ids) {
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = CACHE_NAME, unless = "#result.size() > 1")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithUnless(Collection<CollectionCacheableTestId> ids) {
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = CACHE_NAME, key = "#p0.id")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithKey(Collection<CollectionCacheableTestId> ids) {
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
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
}
