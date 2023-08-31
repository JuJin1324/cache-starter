package starter.cache.user.repository;

import org.springframework.stereotype.Repository;
import starter.cache.user.domain.User;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/08/31
 */

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

    public User findByUsername(String username){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return map.get(username);
    }
}
