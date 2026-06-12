package sc.laplace.test.hillstone.aop.service;

import sc.laplace.test.hillstone.aop.model.User;

import java.util.List;

public interface UserService {
    /**
     * find user list.
     *
     * @return user list
     */
    List<User> findUserList();

    /**
     * add user
     */
    void addUser();
}
