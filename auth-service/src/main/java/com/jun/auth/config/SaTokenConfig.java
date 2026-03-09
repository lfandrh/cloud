package com.jun.auth.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private static final Set<String> EXCLUDE_PATHS = Set.of(
            "/auth/login",
            "/auth/loginByPhone",
            "/auth/refreshToken",
            "/auth/sendCaptcha",
            "/auth/verifyCaptcha"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            String path = SaHolder.getRequest().getRequestPath();
            if (EXCLUDE_PATHS.contains(path)) {
                return;
            }
            StpUtil.checkLogin();
        }));
    }
}
