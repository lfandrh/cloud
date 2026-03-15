package com.jun.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

final class LegacySha256PasswordUtil {

    private static final String PREFIX = "{sha256}";
    private static final String SALT = "jun-cloud-static-salt-v1";

    private LegacySha256PasswordUtil() {
    }

    static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || !storedPassword.startsWith(PREFIX)) {
            return false;
        }
        return encode(rawPassword).equals(storedPassword);
    }

    private static String encode(String rawPassword) {
        return PREFIX + sha256Base64(rawPassword + ":" + SALT);
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

