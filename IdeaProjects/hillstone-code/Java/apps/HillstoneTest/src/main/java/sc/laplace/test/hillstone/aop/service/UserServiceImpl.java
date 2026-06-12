package sc.laplace.test.hillstone.aop.service;

import org.springframework.stereotype.Service;
import sc.laplace.test.hillstone.aop.annotation.UserLogAnnotation;
import sc.laplace.test.hillstone.aop.model.User;

import java.util.Collections;
import java.util.List;

/**
 * @author jxwu
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * find user list.
     *
     * @return user list
     */
    public List<User> findUserList() {
        return Collections.singletonList(new User("pdai", 18));
    }

    /**
     * add user
     */
    @UserLogAnnotation
    public void addUser() {
        // do something
    }

    /**
     * del user
     */
    public void delUser() {
        // do something
    }
}
