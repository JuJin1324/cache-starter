package starter.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CacheStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheStarterApplication.class, args);
    }

}
