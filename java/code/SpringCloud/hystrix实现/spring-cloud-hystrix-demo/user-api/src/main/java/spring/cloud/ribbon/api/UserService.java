package spring.cloud.ribbon.api;

import spring.cloud.ribbon.domain.User;
import java.util.List;

public interface UserService {

    public Boolean saveUser(User user);

    public List<User> getUsers();

}
