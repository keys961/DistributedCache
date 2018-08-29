# README

> This is a project of distributed K-V cache (in the future it may support graph database).
>
> It is based on Spring Cloud.
>
> It may be my graduation project if I decide not to get my master degree.

## Project Structure

This project contains 4 modules:

- `cache-registry`: The micro-service registry service.
- `cache-route`: The server-side load balancing service of the cache service.
- `cache-service`: The cache service deployed on the server provides the cache service.
- `cache-client`: It provides the APIs and the client to access the cache service.

## Examples

### 1. Registry Server

The properties that will be often configured is:

```yaml
server.port: # server port
eureka.client.serviceUrl.defaultZone: # registry url
eureka.instance.hostname: # registry host name
eureka.instance.leaseRenewalInterval: # heart beat frequency
```

You need to start it first.

### 2. Service Route/Gateway

The properties that will be often configured is:

```yaml
server.port: # server port
eureka.client.serviceUrl.defaultZone: # registry server url
loadBalance.virtualNode: # virtual node number for the DHT algorithm
ribbon.readTimeout: # read timeout for forwarding request
ribbon.connectTimeout: # connect timeout for forwarding request
```

You need to start it next.

### 3. Cache Server

An example to configure the cache is shown below:

```yaml
xxcache:
  name: CacheManager
  caches: # Configure 3 caches
    - cacheName: cache1 # Cache name is "cache1"
      cacheType: basic # Using basic cache storage
      expiration:
        strategy: noop # No expiration applied

    - cacheName: cache2
      cacheType: approximate_lru # Using ConcurrentLruMap for cache storage
      maxSize: 1024 # Max size of the cache storage
      expiration:
        expiration: 3600 # Expiration in second
        strategy: lazy # Using lazy strategy to clean expiration items

    - cacheName: cache3
      cacheType: approximate_lru
      maxSize: 2048
      expiration:
        expiration: 1800
        strategy: lazy
```

### 4. Application using Cache Client

Currently, you must using Spring Cloud Eureka to register you client to the cache service cluster using these 3 annotation:

```java
@SpringBootApplication
@EnableDiscoveryClient/@EnableEurekaClient
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

Then in your `pom.xml`, add the dependency:

```xml
<dependency>
    <groupId>org.yejt</groupId>
    <artifactId>cache-client</artifactId>
    <version>${xxcache.version}</version>
    <exclusions>
        <exclusion>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

Next, add annotation `@XXCacheAutoConfiguration` to your application.

Then you can apply the cache to your application, using these 3 annotations:

- `@Cacheable`
- `@CachePut`
- `@CacheRemove`

Next is the example of usage of these 3 annotations in `UserRepository`:

```java
@Repository
public class UserRepository {
    @CachePut(cacheName = "cache1", keyGenerator = UserKeyGenerator.class,
        condition = UserCacheCondition.class)
    public User addUser(User user) {
        //Ommitted...
    }

    @Cacheable(cacheName = "cache1", condition = UserCacheCondition.class)
    public User getUser(String username) {
        // Ommitted...
    }

    @CacheRemove(cacheName = "cache1")
    public User removeUser(String username) {
    	// Ommitted...
    }
}
```

