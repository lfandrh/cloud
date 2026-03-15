package com.jun.auth.service;

import com.jun.auth.dto.TokenResponse;
import com.jun.auth.dto.UserInfoDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return TokenResponse
     */
    TokenResponse login(String username, String password);

    /**
     * 手机号登录
     *
     * @param phone    手机号
     * @param captcha  验证码
     * @return TokenResponse
     */
    TokenResponse loginByPhone(String phone, String captcha);

    TokenResponse refreshToken(String refreshToken);

    void sendCaptcha(String phone);

    boolean verifyCaptcha(String phone, String captcha);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     */
    UserInfoDTO getCurrentUser();
}
