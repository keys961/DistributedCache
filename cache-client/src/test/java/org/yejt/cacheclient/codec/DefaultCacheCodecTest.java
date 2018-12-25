package org.yejt.cacheclient.codec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.yejt.cacheclient.BaseTest;

public class DefaultCacheCodecTest extends BaseTest {
    private DefaultCacheCodec codec = new DefaultCacheCodec();

    @Test
    public void encodeAndDecodeTest() {
        Person person = new Person("id", "name", 1);
        byte[] raw = codec.encode(person);
        Assert.assertNotNull(raw);
        Person person1 = (Person) codec.decode(raw);
        Assert.assertEquals(person, person1);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Person {
        private String id;

        private String name;

        private int age;
    }
}
