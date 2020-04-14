package spring.cloud.service.provider.service;

import org.springframework.stereotype.Service;
import spring.cloud.service.api.UserService;
import spring.cloud.service.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUserService implements UserService {

    private Map<Long,User> userMap = new ConcurrentHashMap<Long,User>();

    @Override
    public Boolean saveUser(User user) {
        return userMap.put(user.getId(),user) == null;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList(userMap.values());
    }
}
