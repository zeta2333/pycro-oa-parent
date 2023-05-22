package pycro.usts.security.custom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pycro.usts.common.utils.MD5;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-22 10:20 AM
 */
@Component
public class CustomMD5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return MD5.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
