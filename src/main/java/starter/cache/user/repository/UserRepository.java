package starter.cache.user.repository;

import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Repository;
import starter.cache.user.domain.User;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/08/31
 */

@CacheConfig(cacheNames = "user")
@Repository
public class UserRepository {
    private final Map<String, User> map;

    public UserRepository() {
        this.map = new HashMap<>();
    }

    @PostConstruct
    public void setUp() {
        String username1 = "username1";
        this.map.put(username1, new User(username1, "이름", 10));
    }

    public User findByUsernameNotCached(String username) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return map.get(username);
    }

    @Cacheable(key = "#username", value = "user")
    public User findByUsername(String username) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return map.get(username);
    }

    @Cacheable(key = "'all'", value = "users")
    public List<User> findAll() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(map.values());
    }

    @CachePut(key = "#user.username", value = "user")
    @CacheEvict(key = "'all'", value = "users")
    public User save(User user) {
        this.map.put(user.getUsername(), user);
        return user;
    }

    @Caching(evict = {
            @CacheEvict(key = "'all'", value = "users"),
            @CacheEvict(key = "#user.username", value = "user")
    })
    public void delete(User user) {
        this.map.remove(user.getUsername());
    }
}
