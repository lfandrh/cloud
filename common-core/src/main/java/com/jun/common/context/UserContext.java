package com.jun.common.context;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

public class UserContext {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    public static Long getUserId() {
        String userId = getHeader(HEADER_USER_ID);
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return Long.parseLong(userId);
    }

    public static String getUsername() {
        return getHeader(HEADER_USERNAME);
    }

    public static List<String> getRoles() {
        String rolesStr = getHeader(HEADER_USER_ROLES);
        if (rolesStr == null || rolesStr.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return List.of(rolesStr.split(","));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static boolean isAdmin() {
        List<String> roles = getRoles();
        return roles.contains("R_ADMIN") || roles.contains("R_SUPER");
    }

    private static String getHeader(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(headerName);
        }
        return null;
    }
}
