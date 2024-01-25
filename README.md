# cache-starter

## Caching
### 개요
> 캐싱(Caching)은 애플리케이션의 처리 속도를 높여준다. 이미 가져온 데이터나 계산된 결과값의 복사본을 저장함으로써 처리 속도를 향상시키며, 
> 이를 통해 향후 요청을 더 빠르게 처리할 수 있다. 대부분의 프로그램이 동일한 데이터나 명령어에 반복해서 엑세스하기 때문에 캐싱은 효율적인 아키텍처 패턴이다.
> 
> 보통 Spring framework 에서 개발을 진행하면서 Cache 는 Local Cache 로 `EhCache` 와 Global Cache 로 `Redis` 를 주로 사용한다.

### 캐시를 적용하기에 적합한 데이터
> * 반복적이고 동일한 결과가 나오는 기능의 반환값
> * 업데이트가 자주 발생하지 않는 데이터
> * 자주 조회되는 데이터
> * 입력값과 출력값이 일정한 데이터
> * 캐싱된 데이터는 데이터 갱신으로 인해 DB와 불일치가 발생할 수 있다. 
> 그렇기 때문에 데이터 Update 가 잦게 일어나거나 데이터 불일치시 비즈니스 로직 상 문제가 발생할 수 있는 기능은 캐싱 대상으로 적합하지 않다.

### Local Cache vs Global Cache
> **Local Cache**  
> 장점  
> * WAS의 인스턴스 메모리에 데이터를 저장하기 때문에 접근 속도가 매우 빠르다.
> * 별도의 Infrastructure 를 필요로 하지 않는다.
> 
> 단점  
> * 캐싱되는 데이터가 커지면 WAS 인스턴스의 메모리 사용량도 증가한다.
> * 외부에서 참조하기가 힘들기 때문에 인스턴스가 여러 개인 경우 캐싱된 데이터의 원본 데이터가 바뀌면 정합성을 보장하지 못한다.
> 
> **Cache Server (aka Global Cache)**  
> 장점  
> * 여러 인스턴스에서 동일한 값을 바라본다. 그렇기 때문에 서버의 대수가 많아지면 많아질수록 Local Cache 에 비해 유리하다.
> * Shading 과 Replication 으로 분산 저장이 가능하다.
> 
> 단점  
> * 별도의 Infrastructure 가 필요하다.
> * 네트워크 비용이 발생하기 때문에 상대적으로 Local Cache보다는 느리다.

### 참조사이트
> [Local Cache, Global Cache 차이](https://goldfishhead.tistory.com/29)  
> [Java Application 성능개선에 대해 알아보자 - Local Cache 편](https://dev.gmarket.com/16)  
> [캐시(Cache) Local Cache & Global Cache](https://dev-jj.tistory.com/entry/캐시Cache-Local-Cache-Global-Cache)

---

## Spring Cache
### 개요
> Spring은 일부 데이터를 미리 메모리 저장소에 저장하고 저장된 데이터를 다시 읽어 사용하는 캐시 기능을 제공한다. 
> 트랜잭션과 마찬가지로 AOP를 사용하여 캐시 기능을 구현하였고, 캐시 애너테이션을 사용하면 쉽게 구현할 수 있다. 
> Spring에서 캐시 데이터를 관리하는 기능은 별도의 캐시 프레임워크에 위임한다.  
> 
> 캐시 저장소를 구성하는 방식은 두 가지로 구분된다.  
> * Java 애플리케이션에 embedded하는 방식(로컬 캐시)
> * 애플리케이션 외부의 독립 메모리 저장소를 별도로 구축하여 모든 인스턴스가 네트워크를 사용하여 데이터를 캐시하는 방식(원격 캐시)

### Cache Manager
> 캐시 추상화에서는 캐시 기술을 지원하는 캐시 매니저를 Bean으로 등록해야 한다.
> * ConcurrentMapCacheManager: JRE에서 제공하는 ConcurrentHashMap을 캐시 저장소로 사용할 수 있는 구현체다. 
> 캐시 정보를 Map 타입으로 메모리에 저장해두기 때문에 빠르고 별다른 설정이 필요 없다는 장점이 있지만, 실제 서비스에서 사용하기엔 기능이 빈약하다.
> * SimpleCacheManager: 기본적으로 제공하는 캐시가 없다. 사용할 캐시를 직접 등록하여 사용하기 위한 캐시 매니저 구현체다.
> * EhCacheCacheManager: Java에서 유명한 캐시 프레임워크 중 하나인 EhCache를 지원하는 캐시 매니저 구현체다.
> * CaffeineCacheManager: Java 8로 Guava 캐시를 재작성한 Caffeine 캐시 저장소를 사용할 수 있는 구현체다. 
> EhCache 와 함께 인기 있는 매니저인데, 이보다 좋은 성능을 갖는다고 한다.
> * JCacheCacheManager: JSR-107 표준을 따르는 JCache 캐시 저장소를 사용할 수 있는 구현체다.
> * RedisCacheManager: Redis를 캐시 저장소로 사용할 수 있는 구현체다.
> * CompositeCacheManager: 한 개 이상의 캐시 매니저를 사용할 수 있는 혼합 캐시 매니저다.

### Dependencies
> ```groovy
> dependencies {
>     implementation 'org.springframework.boot:spring-boot-starter-cache'
>     //  JSR-107 API를 사용하기 위해서 추가
>     implementation 'javax.cache:cache-api:1.1.1'
> }
> ```

### @EnableCaching
> Spring 에서 캐시를 사용하기 위해서는 `@EnableCaching` 애노테이션의 선언이 필요하다.  
> ```java
> @Configuration
> @EnableCaching
> public class CacheConfig {
>     ...
> }
> ```

### @Cacheable
> 캐시가 사용되어야할 필요가 있는 조회 메서드에 `@Cacheable` 애노테이션을 붙인다.  
> * 데이터를 캐시에 저장
> * 메서드를 호출할 때 캐시의 이름 (value) 과 키 (key) 를 확인하여 이미 저장된 데이터가 있으면 해당 데이터를 리턴
> * 만약 데이터가 없다면 메서드를 수행 후 결과값을 저장
> ```java
> @Repository
> public class UserRepository { 
>     ...
>     @Cacheable(key = "#username", value = "user")
>     public User findByUsername(String username){
>         try {
>             Thread.sleep(2000);
>         } catch (InterruptedException e) {
>             throw new RuntimeException(e);
>         }
> 
>         return map.get(username);
>     }
>     ...
> }
> ```
> `key`: 동적인 키 값을 사용하는 SpEL 표현식. 동일한 cache name 을 사용하지만 구분될 필요가 있을 때 사용되는 값.  
> `value`: cacheName 의 alias  	
> `cacheName`: 캐시 이름(설정 메서드 리턴값이 저장되는)  
> `condition`: SpEL 표현식이 참일 경우에만 캐싱 적용. or, and 등 조건식 및 논리연산 가능.  
> `unless`: 캐싱을 막기 위해 사용되는 SpEL 표현식. condition과 반대로 참일 경우에만 캐싱이 적용되지 않음.  
> `cacheManager`: 사용 할 CacheManager 지정.  
> `sync`: 여러 스레드가 동일한 키에 대한 값을 로드하려고 할 경우, 기본 메서드의 호출을 동기화함. 캐시 구현체가 Thread safe 하지 않는 경우, 캐시에 동기화를 걸 수 있는 속성.  

### @CachePut
> * @Cacheable 과 비슷하게 데이터를 캐시에 저장
> * 차이점은 @Cacheable 은 캐시에 데이터가 이미 존재하면 메서드를 수행하지 않지만 @CachePut 은 항상 메서드를 수행
> * 그래서 주로 캐시 데이터를 갱신할 때 많이 사용
> ```java
> @Repository
> public class UserRepository { 
>     ...
>     @CachePut(key = "#user.username", value = "user")
>     @CacheEvict(key = "'all'", value = "users")
>     public User save(User user) {
>         this.map.put(user.getUsername(), user);
>         return user;
>     }
>     ...
> } 
> ```

### @CacheEvict
> 캐시에 있는 데이터를 삭제.  
> ```java
> @Repository
> public class UserRepository { 
>     ...
>     @CachePut(key = "#user.username", value = "user")
>     @CacheEvict(key = "'all'", value = "users")
>     public User save(User user) {
>         this.map.put(user.getUsername(), user);
>         return user;
>     }
>     ...
> } 
> ```

### @Caching
> Cacheable, CachePut, CacheEvict 를 여러 개 사용할 때 묶어주는 기능.
> ```java
> @Repository
> public class UserRepository {
>     ...
>     @Caching(evict = {
>             @CacheEvict(key = "'all'", value = "users"),
>             @CacheEvict(key = "#user.username", value = "user")
>     })
>     public void delete(User user) {
>         this.map.remove(user.getUsername());
>     }
>     ...
> }
> ```

### 참조사이트
> [Spring Cache에 대해 알아보자](https://velog.io/@songs4805/Spring-Cache에-대해-알아보자)  
> [Spring Boot 에서 Cache 사용하기](https://bcp0109.tistory.com/385)

---

## EhCache
### 개요
> ehcache 는 Spring 에서 간단하게 사용할 수 있는 Java 기반 오픈소스 캐시 라이브러리이다.
> 
> redis나 memcached같은 캐시 엔진들도 있지만, 저 2개의 캐시 엔진과는 달리 ehcache는 데몬을 가지지 않고 Spring 내부적으로 동작하여 캐싱 처리를 한다.
> 따라서 redis같이 별도의 서버를 사용하여 생길 수 있는 네트워크 지연 혹은 단절같은 이슈에서 자유롭고 같은 로컬 환경 일지라도 별도로 구동하는 memcached 와는 다르게 
> ehcache 는 서버 어플리케이션과 라이프사이클을 같이 하므로 사용하기 더욱 간편하다.  
> 
> ehcache는 2.x 버전과 3 버전의 차이가 크다.
> 3 버전 부터는 javax.cache API (JSR-107)와의 호환성을 제공한다. 따라서 표준을 기반으로 만들어졌다고 볼 수 있다.

### Dependencies
> ```groovy
> dependencies {
>     implementation 'org.springframework.boot:spring-boot-starter-cache'
>     //  JSR-107 API를 사용하기 위해서 추가
>     implementation 'javax.cache:cache-api:1.1.1'
>     implementation 'org.ehcache:ehcache:3.10.8'
> }
> ```

### ehcache.xml
> `resources` 디렉터리 하위에 `ehcache.xml` 파일을 추가한다.  
> ```xml
> <config xmlns='http://www.ehcache.org/v3'>
> 
>     <cache alias="user">
>         <key-type>java.lang.String</key-type>
>         <value-type>starter.cache.user.domain.User</value-type>
>         <expiry>
>             <ttl unit="minutes">30</ttl>
>         </expiry>
>         <resources>
>             <offheap unit="MB">10</offheap>
>         </resources>
>     </cache>
> 
>     <cache alias="users">
>         <key-type>java.lang.String</key-type>
>         <value-type>java.util.List</value-type>
>         <expiry>
>             <ttl unit="minutes">30</ttl>
>         </expiry>
>         <resources>
>             <offheap unit="MB">10</offheap>
>         </resources>
>     </cache>
> 
> </config>
> ```
> 
> **<cache> 태그**  
> alias: 캐시를 적용할 `@Cacheable` 애노테이션에서 value 로 들어갈 별칭    
> `<key-type>`: 캐싱할 값의 키  
> `<value-type>`: 캐싱할 값, 단 캐싱을 위해서는 해당 클래스에 `Serializable` 이 구현되어 있어야함.  
> `<expiry>`: 캐시의 유효 기간  
> `<resources>`: 캐시 데이터의 저장 공간과 용량을 지정한다. 만약 힙 메모리만 사용한다면 `<heap>` 요소만으로 대체할 수 있다.  
> `<heap>`: heap은 JVM 힙 메모리에 캐시를 저장하도록 세팅하는 요소. Deprecated 된 것으로 봐서는 앞으로는 off heap 에만 캐시를 저장하려는 듯하다.    
> `<offheap>`: offheap이란 말 그대로 힙 메모리를 벗어난 메모리로 Java GC에 의해 데이터가 정리되지 않는 공간입니다.  

### application.yml
> ```yaml
> spring:
>     cache:
>         jcache:
>             config: classpath:ehcache.xml
> ```

### 참조사이트
> [Spring 로컬 캐시 라이브러리 ehcache](https://medium.com/finda-tech/spring-로컬-캐시-라이브러리-ehcache-4b5cba8697e0)    
> [ehcache 공식 사이트](https://www.ehcache.org)  
> [Spring - Ehcache](https://backtony.github.io/spring/2022-06-14-spring-ehcache/)

---

## Redis Cache
### Dependencies
> ```groovy
> dependencies {
>     implementation 'org.springframework.boot:spring-boot-starter-data-redis'
>     implementation 'org.springframework.boot:spring-boot-starter-cache'
> }
> ```

### application.yml
> ```yaml
> spring:
>     cache:
>         type: redis
>     redis:
>         host: localhost
>         port: 6379
> ```

### Configuration.java
> ```java
> @Configuration
> @EnableCaching
> public class CacheConfig {
> 
>     @Bean
>     public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
>         RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
>             .entryTtl(Duration.ofHours(1)) // 캐시 만료 시간 설정
>             .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
>             .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
>             .disableCachingNullValues();
> 
>         return RedisCacheManager.builder(redisConnectionFactory)
>             .cacheDefaults(cacheConfiguration)
>             .build();
>     }
> }
> ```

---
