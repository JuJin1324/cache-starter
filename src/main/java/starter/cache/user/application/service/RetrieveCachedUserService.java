package starter.cache.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import starter.cache.user.domain.User;
import starter.cache.user.repository.UserRepository;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/08/31
 */

@Service
@RequiredArgsConstructor
public class RetrieveCachedUserService {
    private final UserRepository userRepository;

    @Cacheable(value = "user", key = "#username")
    public User retrieve(String username) {
        return userRepository.findByUsername(username);
    }
}
