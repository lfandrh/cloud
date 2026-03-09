package com.jun.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordUtil {

    private static final String PREFIX = "{sha256}";
    private static final String SALT = "jun-cloud-static-salt-v1";

    private PasswordUtil() {
    }

    public static String encode(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return PREFIX + sha256Base64(rawPassword + ":" + SALT);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith(PREFIX)) {
            return encode(rawPassword).equals(storedPassword);
        }
        // Backward compatibility for legacy plain-text data
        return rawPassword.equals(storedPassword);
    }

    private static String sha256Base64(String val) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(val.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }
}
