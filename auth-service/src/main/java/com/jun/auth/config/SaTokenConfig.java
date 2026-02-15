package com.jun.auth.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 除了公开接口外，其他接口都需要登录
            String path = SaHolder.getRequest().getRequestPath();
            if (path.startsWith("/auth/login") ||
                    path.startsWith("/auth/sendCaptcha") ||
                    path.startsWith("/auth/verifyCaptcha")) {
                return; // 白名单放行
            }
            StpUtil.checkLogin();
        }));
    }
}
