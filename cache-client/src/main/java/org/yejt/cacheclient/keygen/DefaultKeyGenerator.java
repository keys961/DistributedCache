package org.yejt.cacheclient.keygen;

/**
 * Default key generator, just append the string of each params.
 *
 * @author yejt
 */
public class DefaultKeyGenerator implements KeyGenerator {
    @Override
    public String generateKey(Object target, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object param : params)
            stringBuilder.append(param.toString());

        return stringBuilder.toString();
    }
}
