package org.yejt.cachedemo.service;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yejt.cachedemo.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keys961
 */
@Component
public class UserServiceRunner {
    private final UserService userService;

    private Set<User> users = new HashSet<>();

    @Autowired
    public UserServiceRunner(UserService userService) {
        users.add(new User("keys961", "123456"));
        users.add(new User("fucker0x22", "123456"));
        users.add(new User("asshole123bbb", "123456"));
        this.userService = userService;
        new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new DefaultThreadFactory("test-pool"))
                .submit(this::runTest);
    }

    private void runTest() {
        try {
            Thread.sleep(1000);
            userService.put("test", new User("test", "test123"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            for (User user : users) {
                try {
                    userService.put(user.getUsername(), user);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 2; i++) {
                for (User user : users) {
                    User u = null;
                    try {
                        u = userService.get(user.getUsername());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    assert (u != null && user.getUsername().equals(u.getUsername()));
                }
            }

            for (User user : users) {
                try {
                    userService.remove(user.getUsername());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (User user : users) {
                try {
                    User u = userService.get(user.getUsername());
                    assert (u == null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
