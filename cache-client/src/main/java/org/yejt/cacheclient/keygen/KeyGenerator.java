package org.yejt.cacheclient.keygen;

/**
 * Key generator interface to generate the cache key.
 *
 * @author keys961
 */
public interface KeyGenerator {

    /**
     * Generate the key for caching.
     *
     * @param target: Method return value
     * @param params: Method params
     * @return The generated key in {@link String}
     */
    String generateKey(Object target, Object... params);
}
