package de.qaware.tools.collectioncacheableforspring;

import de.qaware.tools.collectioncacheableforspring.creator.DefaultCollectionCreator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionCacheableOperationTest {

    @Test
    public void testToStringAndEqualsAndHashCode() {
        CollectionCacheableOperation.Builder builder = new CollectionCacheableOperation.Builder();

        builder.setName("name");
        builder.setCacheNames("cacheName1", "cacheName2");
        builder.setCondition("condition");
        builder.setKey("key");
        builder.setKeyGenerator("keyGenerator");
        builder.setCacheManager("cacheManager");
        builder.setCacheResolver("cacheResolver");
        builder.setUnless("unless");
        builder.setFindAll(true);
        builder.setPutNull(true);
        builder.setCollectionCreator(new DefaultCollectionCreator());

        CollectionCacheableOperation collectionCacheableOperation1 = builder.build();

        builder.setCacheNames("cacheName1");
        CollectionCacheableOperation collectionCacheableOperation2 = builder.build();

        assertThat(collectionCacheableOperation1)
                .hasToString("Builder[name] caches=[cacheName1, cacheName2] | key='key' | keyGenerator='keyGenerator' | cacheManager='cacheManager' | cacheResolver='cacheResolver' | condition='condition' | unless='unless' | isFindAll=true | putNull=true | collectionCreator=DefaultCollectionCreator")
                .isNotEqualTo(new Object())
                .isNotEqualTo(collectionCacheableOperation2)
                .isEqualTo(collectionCacheableOperation1);
    }
}