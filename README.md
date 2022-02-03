# Collection Cacheable for Spring

[![Build Status](https://github.com/qaware/collection-cacheable-for-spring/workflows/build/badge.svg?branch=master)](https://github.com/qaware/collection-cacheable-for-spring/actions?query=workflow%3A%22build%22)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=qaware_collection-cacheable-for-spring&metric=alert_status)](https://sonarcloud.io/dashboard?id=qaware_collection-cacheable-for-spring)
[![Code Coverage](https://sonarcloud.io/api/project_badges/measure?project=qaware_collection-cacheable-for-spring&metric=coverage)](https://sonarcloud.io/dashboard?id=qaware_collection-cacheable-for-spring)
[![Maven Central](https://img.shields.io/maven-central/v/de.qaware.tools.collection-cacheable-for-spring/collection-cacheable-for-spring-starter)](https://mvnrepository.com/artifact/de.qaware.tools.collection-cacheable-for-spring/collection-cacheable-for-spring-starter)

This library provides the `@CollectionCacheable` annotation extending Spring's caching mechanism, in
particular `@Cacheable`.

`@CollectionCacheable` supports putting a whole collection of entities as single cache items, thus enabling an efficient
integration of batch retrieval of entities. See the example usage below for a detailed explanation.

## Getting started

Inside your Spring Boot application, add the following (maven) dependency:

```
<dependency>
    <groupId>de.qaware.tools.collection-cacheable-for-spring</groupId>
    <artifactId>collection-cacheable-for-spring-starter</artifactId>
    <version>1.3.0</version>
</dependency>
```

You may also use the separate artifact `collection-cacheable-for-spring-api`
providing the annotation only with minimal dependencies.

## Example usage

Suppose your entity looks as follows,

```java
class MyEntity {
    long id;
    String value;
}
```

and you have defined a method to retrieve one and many values by their unique `id` as follows:

```java
class MyRepository {
    @Nullable
    MyEntity findById(long id) {
        // retrieve one MyEntity from persistence layer (if existing)
    }

    // map key is MyEntity.id
    Map<Long, MyEntity> findByIds(Collection<Long> ids) {
        // do efficient batch retrieve of many MyEntity's and build result map
    }
} 
```

Now, to efficiently implement a cache on `MyRepository`, you want the following to happen:

* Whenever a call to `findById` occurs, either a cache hit should be returned, or the method is called putting the
  result in the cache unless it is `null`.

* Whenever a call to `findByIds` occurs, the method should be called with the non-empty subset of the given `ids`
  parameter which are **not** in the cache. Otherwise, the cache hits are simply returned.

To illustrate the above behavior further, consider the following call sequence:

1. `findById(1)` retrieves an entity from the persistence layer and fills the cache for id `1`
1. `findByIds({1, 2})` finds the cache hit for id `1` and only calls `findByIds({2})` as a delegate. It then fills the
   cache for id `2`.
1. `findById(2)` just retrieves the cache hit for id `2`.
1. `findByIds({1, 2})` will not call anything and just returns a map built from the cache hits for id `1` and `2`.

In order to implement exactly this behavior, this library is used as follows:

```java
class MyRepository {
    @Nullable
    @Cacheable(cacheNames = "myCache", unless = "#result == null")
    MyEntity findById(long id) {
        // retrieve one MyEntity from persistence layer (if existing)
    }

    @CollectionCacheable(cacheNames = "myCache")
    Map<Long, MyEntity> findByIds(Collection<Long> ids) {
        // do efficient batch retrieve of many MyEntity's and build result map
    }
} 
```

See [this test repository](collection-cacheable-for-spring-starter/src/test/java/de/qaware/tools/collectioncacheableforspring/CollectionCacheableTestRepository.java)
for a completely worked out example.

## Advanced usage

### Additional "findAll" method

If your repository also provides a "findAll"-like method without any arguments, you can integrate this as well into your
cache as follows:

```java
class MyRepository {

    @CollectionCacheable(cacheNames = "myCache")
        // map key is MyEntity.id
    Map<Long, MyEntity> findAll() {
        // do efficient batch retrieve of many MyEntity's and build result map
    }
} 
```

The library assumes that such methods do not have any arguments. Note that the return value must still be a `Map`,
otherwise the library is unable to determine the cache id.

### Also consider `null` as cache hit

Under some circumstances, it is also desirable to cache also `null` results. This must be explicitly enabled via
the `putNull` flag on batch retrieval as follows:

```java
class MyRepository {
    @CollectionCacheable(cacheNames = "myCache", putNull = true)
    Map<Long, MyEntity> findByIds(Collection<Long> ids) {
        // do efficient batch retrieve of many MyEntity's and build result map
    }
} 
```

### Using Set or List as method argument

The methods annotated with `@CollectionCacheable` use the base interface `Collection` in the above examples, but
also `List<>` and `Set<>` interfaces are supported. Note though that this may change the actual passed implementation
to `LinkedList` or `HashSet`, respectively (see `DefaultCollectionCreator` and `SetCollectionCreator` implementations).
You can add support for more collection-like types by providing beans deriving from `CollectionCreator`, or even
override the given creators thanks to Spring Boot autoconfiguration.

## Contributing

Please report [issues or feature requests](https://github.com/qaware/collection-cacheable-for-spring/issues).

Also see this [Spring issue here](https://github.com/spring-projects/spring-framework/issues/23221). As of now,
integration into Spring is not ideal, so this library might break with future Spring releases.