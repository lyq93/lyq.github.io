package spring.cloud.service.fallback;

import spring.cloud.service.api.UserService;
import spring.cloud.service.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 针对UserService接口的异常fallback
 */
public class UserServiceFallBack implements UserService {
    @Override
    public Boolean saveUser(User user) {
        return false;
    }

    @Override
    public List<User> getUsers() {
        return Collections.emptyList();
    }
}
