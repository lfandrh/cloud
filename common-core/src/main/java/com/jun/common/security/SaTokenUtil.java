package com.jun.common.security;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Sa-Token 工具类
 */
public class SaTokenUtil {

    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        // 优先从请求头获取（网关传递）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            if (userId != null && !userId.isEmpty()) {
                return userId;
            }
        }
        // 备用：从 Sa-Token 获取
        return (String) StpUtil.getLoginId();
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        // 优先从请求头获取
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String username = request.getHeader("X-Username");
            if (username != null && !username.isEmpty()) {
                return username;
            }
        }
        // 备用：从 Sa-Token 获取
        return (String) StpUtil.getLoginId();
    }

    /**
     * 获取当前用户信息
     */
    public static CurrentUser getCurrentUser() {
        CurrentUser user = new CurrentUser();
        user.setId(getUserId());
        user.setUsername(getUsername());
        return user;
    }

    /**
     * 检查是否已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }
}
