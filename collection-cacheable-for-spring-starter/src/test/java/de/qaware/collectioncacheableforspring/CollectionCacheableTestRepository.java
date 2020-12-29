package de.qaware.collectioncacheableforspring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


public class CollectionCacheableTestRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionCacheableTestRepository.class);

    private final CollectionCacheableTestDbRepository myDbRepository;

    public CollectionCacheableTestRepository(CollectionCacheableTestDbRepository myDbRepository) {
        this.myDbRepository = myDbRepository;
    }

    @Cacheable(cacheNames = "myCache")
    public CollectionCacheableTestValue findById(CollectionCacheableTestId id) {
        LOGGER.info("Getting value for id={}", id);
        return myDbRepository.findById(id);
    }

    @Cacheable(cacheNames = "myCache", key = "#id.id")
    public CollectionCacheableTestValue findByIdWithKey(CollectionCacheableTestId id) {
        LOGGER.info("Getting value with key for id={}", id);
        return myDbRepository.findById(id);
    }

    @CollectionCacheable("myCache")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIds(Collection<CollectionCacheableTestId> ids) {
        LOGGER.info("Getting mapped values for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = "myCache", condition = "#ids.size() < 3")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithCondition(Collection<CollectionCacheableTestId> ids) {
        LOGGER.info("Getting mapped values with condition for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = "myCache", unless = "#result.size() > 1")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithUnless(Collection<CollectionCacheableTestId> ids) {
        LOGGER.info("Getting mapped values with unless for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = "myCache", key = "#p0.id")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findByIdsWithKey(Collection<CollectionCacheableTestId> ids) {
        LOGGER.info("Getting mapped values with key for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable("myCache")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAll() {
        LOGGER.info("Getting all values");
        return myDbRepository.findAll();
    }

    @CollectionCacheable(cacheNames = "myCache", unless = "#result.size() > 1")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAllWithUnless() {
        LOGGER.info("Getting all values with unless");
        return myDbRepository.findAll();
    }

    @CollectionCacheable(cacheNames = "myCache", key = "#result.id")
    public Map<CollectionCacheableTestId, CollectionCacheableTestValue> findAllWithKey() {
        LOGGER.info("Getting all values with condition");
        return myDbRepository.findAll();
    }
}
