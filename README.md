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
>     implementation 'org.ehcache:ehcache:3.10.8'
>     //  JSR-107 API를 사용하기 위해서 추가
>     implementation 'javax.cache:cache-api:1.1.1'
> }
> ```

### ehcache.xml
> `resources` 디렉터리 하위에 `ehcache.xml` 파일을 추가한다.  
> ```xml
> <config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>         xmlns="http://www.ehcache.org/v3"
>         xmlns:jsr107="http://www.ehcache.org/v3/jsr107"
>         xsi:schemaLocation="
>             http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd
>             http://www.ehcache.org/v3/jsr107 http://www.ehcache.org/schema/ehcache-107-ext-3.0.xsd">
> 
>     <cache alias="user">
>         <key-type>java.lang.String</key-type>
>         <value-type>starter.cache.user.domain.User</value-type>
>         <expiry>
>             <ttl unit="minutes">30</ttl>
>         </expiry>
> 
>         <resources>
>             <offheap unit="MB">10</offheap>
>         </resources>
>     </cache>
> </config>
> ```
> 
> **<cache> 태그**  
> alias: 캐시를 적용할 `@Cacheable` 애노테이션에서 value 로 들어갈 별칭    
> `<key-type>`: 캐싱할 값의 키  
> `<value-type>`: 캐싱할 값  
> `<expiry>`: 캐시의 유효 기간  
> `<resources>`: TODO
> `<offheap>`: TODO

### application.yml
> ```yaml
> spring:
>     cache:
>         jcache:
>             config: classpath:ehcache.xml
> ```

### Main Class
> `@EnableCaching` 애노테이션 추가
> ```java
> @EnableCaching
> @SpringBootApplication
> public class CacheStarterApplication {
> 
>     public static void main(String[] args) {
>         SpringApplication.run(CacheStarterApplication.class, args);
>     }
> 
> }
> ```

### Cacheable
> 캐시가 사용되어야할 필요가 있는 조회 서비스의 메서드에 `@Cacheable` 애노테이션을 붙인다.  
> ```java
> @Service
> @RequiredArgsConstructor
> public class RetrieveCachedUserService {
>     private final UserRepository userRepository;
> 
>     @Cacheable(value = "user", key = "#username")
>     public User retrieve(String username) {
>         return userRepository.findByUsername(username);
>     }
> }
> ```
> value: ehcache.xml 에서 cache 태그의 alias 값.  
> key: Java 메서드에서 사용하는 변수 값(ehcache.xml 의 cache 태그 아래 key-type 태그의 설정 값과 변수 타입이 같아야함.), 앞에 # 붙여야함.  

### 참조사이트
> [Spring 로컬 캐시 라이브러리 ehcache](https://medium.com/finda-tech/spring-로컬-캐시-라이브러리-ehcache-4b5cba8697e0)    
> [ehcache 공식 사이트](https://www.ehcache.org)  
