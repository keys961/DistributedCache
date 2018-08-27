package org.yejt.cachedemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yejt.cachedemo.entity.User;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserServiceRunner
{
    private final UserService userService;

    private Set<User> users = new HashSet<>();

    @Autowired
    public UserServiceRunner(UserService userService)
    {
        users.add(new User("keys961", "123456"));
        users.add(new User("keys962", "123456"));
        users.add(new User("keys963", "123456"));
        this.userService = userService;
        new Thread(this::runTest).start();
    }

    private void runTest()
    {
        while (true)
        {
            for(User user : users)
            {
                try
                {
                    userService.put(user.getUsername(), user);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < 2; i++)
            {
                for(User user : users)
                {
                    User u = null;
                    try
                    {
                        u = userService.get(user.getUsername());
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    assert (u != null && user.getUsername().equals(u.getUsername()));
                }
            }

            for(User user : users)
            {
                try
                {
                    userService.remove(user.getUsername());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            for(User user : users)
            {
                try
                {
                    User u = userService.get(user.getUsername());
                    assert (u == null);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

}
