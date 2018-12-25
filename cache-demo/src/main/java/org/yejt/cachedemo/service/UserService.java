package org.yejt.cachedemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yejt.cachedemo.annotation.Log;
import org.yejt.cachedemo.entity.User;
import org.yejt.cachedemo.repository.UserRepository;

/**
 * @author keys961
 */
@Service
public class UserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Log
    public String sayHello(String name) {
        return "hello," + name + "!";
    }

    @Log
    User get(String username) throws InterruptedException {
        return repository.getUser(username);
    }

    @Log
    User put(String username, User user)
            throws InterruptedException {
        return repository.addUser(user);
    }

    @Log
    User remove(String username) throws InterruptedException {
        return repository.removeUser(username);
    }
}
