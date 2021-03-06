package org.yejt.cachedemo.repository;

import org.springframework.stereotype.Repository;
import org.yejt.cacheclient.annotation.CachePut;
import org.yejt.cacheclient.annotation.CacheRemove;
import org.yejt.cacheclient.annotation.Cacheable;
import org.yejt.cachedemo.condition.UserCacheCondition;
import org.yejt.cachedemo.entity.User;
import org.yejt.cachedemo.keygen.UserKeyGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author keys961
 */
@Repository
public class UserRepository {
    private Map<String, User> userMap = new ConcurrentHashMap<>();

    @CachePut(cacheName = "cache3", keyGenerator = UserKeyGenerator.class,
            condition = UserCacheCondition.class)
    public User addUser(User user) throws InterruptedException {
        Thread.sleep(400);
        userMap.put(user.getUsername(), user);

        return user;
    }

    @Cacheable(cacheName = "cache3", condition = UserCacheCondition.class)
    public User getUser(String username) throws InterruptedException {
        Thread.sleep(400);
        return userMap.get(username);
    }

    @CacheRemove(cacheName = "cache3")
    public User removeUser(String username) throws InterruptedException {
        Thread.sleep(400);
        return userMap.remove(username);
    }
}
