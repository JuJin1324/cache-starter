package starter.cache.user.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/08/31
 */

@SpringBootTest
class RetrieveUserServiceTest {
    @Autowired
    private RetrieveUserService retrieveUserService;
    @Autowired
    private RetrieveCachedUserService retrieveCachedUserService;

    @Test
    void testNotCached() {
        // 한 요청 당 2초가 걸리니 총 4초가 걸림
        retrieveUserService.retrieve("username1");
        retrieveUserService.retrieve("username1");
    }

    @Test
    void testCached() {
        // 한 요청 당 2초가 걸리나 첫번째 요청만 2초가 걸리고
        retrieveCachedUserService.retrieve("username1");
        // 두번째 요청은 캐시에서 가져와서 밀리초가 걸림
        retrieveCachedUserService.retrieve("username1");
    }
}
