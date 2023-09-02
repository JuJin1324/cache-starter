package starter.cache.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import starter.cache.user.domain.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/09/02
 */

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(new User("username1", "name1", 10));
        userRepository.save(new User("username2", "name2", 11));
        userRepository.save(new User("username3", "name3", 12));
        userRepository.save(new User("username4", "name4", 13));
        userRepository.save(new User("username5", "name5", 14));
    }

    @Test
    @DisplayName("캐시되지 않은 호출은 각 2초씩 해서 총 4초가 걸린다.")
    void findByUsernameNotCached() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        userRepository.findByUsernameNotCached("username1");
        userRepository.findByUsernameNotCached("username2");

        stopWatch.stop();
        assertTrue(stopWatch.getTotalTimeSeconds() >= 4.0);
    }

    @Test
    @DisplayName("setUp 에서 save 메서드를 통해서 이미 캐시되어 있어서 첨부터 밀리초가 걸린다.")
    void findByUsername() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        userRepository.findByUsername("username1");
        userRepository.findByUsername("username2");

        stopWatch.stop();
        assertTrue(stopWatch.getTotalTimeSeconds() < 1.0);
    }

    @Test
    @DisplayName("처음 호출에만 2초가 걸리고 그 후 캐시되어서 밀리초가 걸린다.")
    void findAll() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        userRepository.findAll();
        userRepository.findAll();

        stopWatch.stop();
        assertTrue(stopWatch.getTotalTimeSeconds() < 3.0);
    }

    @Test
    @DisplayName("save 한 회원은 캐시에 저장하기 때문에 단건 조회시 캐시에서 조회, 모두 조회는 캐시가 초기화되어서 2초가 걸린다.")
    void save() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        User user = userRepository.save(new User("newUsername", "newName", 100));
        userRepository.findByUsername(user.getUsername());

        stopWatch.stop();
        assertTrue(stopWatch.getTotalTimeSeconds() < 1.0);

        stopWatch.start();
        userRepository.findAll();
        stopWatch.stop();
        assertTrue(stopWatch.getTotalTimeSeconds() >= 2.0);
    }

    @Test
    @DisplayName("delete 한 회원은 캐시에 저장하기 때문에 단건 조회 캐시에서 제거, 모두 조회는 캐시가 초기화되어서 2초가 걸린다.")
    void delete() {
        User user = userRepository.save(new User("newUsername", "newName", 100));
        userRepository.delete(user);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        userRepository.findAll();
        stopWatch.stop();
        assertTrue(stopWatch.getTotalTimeSeconds() >= 2.0);
    }
}
