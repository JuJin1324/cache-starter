package starter.cache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/09/02
 */

@Configuration
@EnableCaching
public class CacheConfig {
    // application.yml
    // spring.cache.jcache.config: ehcache.xml 로 대체
}
