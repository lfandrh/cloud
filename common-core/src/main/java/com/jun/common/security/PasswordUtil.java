package com.jun.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtil {

    private static final String BCRYPT_PREFIX = "{bcrypt}";
    private static final String SHA256_PREFIX = "{sha256}";
    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    public static String encode(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return BCRYPT_PREFIX + BCRYPT.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith(BCRYPT_PREFIX)) {
            return BCRYPT.matches(rawPassword, storedPassword.substring(BCRYPT_PREFIX.length()));
        }
        if (storedPassword.startsWith(SHA256_PREFIX)) {
            return LegacySha256PasswordUtil.matches(rawPassword, storedPassword);
        }
        // One-time migration compatibility for legacy plain-text data.
        return rawPassword.equals(storedPassword);
    }

    public static boolean needsUpgrade(String storedPassword) {
        return storedPassword != null && !storedPassword.startsWith(BCRYPT_PREFIX);
    }
}
