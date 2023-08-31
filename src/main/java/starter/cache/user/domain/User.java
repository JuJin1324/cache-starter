package starter.cache.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/08/31
 */

@AllArgsConstructor
@Getter
public class User implements Serializable {
    private String username;
    private String name;
    private int age;
}
