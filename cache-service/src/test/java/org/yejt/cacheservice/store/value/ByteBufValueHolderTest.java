package org.yejt.cacheservice.store.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

public class ByteBufValueHolderTest
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class User
    {
        String username;

        String password;

        Department department;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Department
    {
        String name;

        String address;
    }

    private User user = new User("Yejt", "123456",
            new Department("CS", "Yuquan, Hangzhou"));

    @Test
    public void testStoreAndFetchValue()
    {
        ValueHolder<User> userValueHolder = new ByteBufValueHolder<>(user);

        Assert.assertTrue(userValueHolder.getTimestamp() > 0);
        Assert.assertEquals("CS", userValueHolder.value().department.name);
    }
}
