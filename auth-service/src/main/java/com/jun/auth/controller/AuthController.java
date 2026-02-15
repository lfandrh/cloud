package com.jun.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jun.auth.dto.TokenResponse;
import com.jun.auth.dto.UserInfoDTO;
import com.jun.auth.service.AuthService;
import com.jun.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户名密码登录
     */
    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        TokenResponse token = authService.login(userName, password);
        return Result.success(token);
    }

    /**
     * 手机号登录
     */
    @PostMapping("/loginByPhone")
    public Result<TokenResponse> loginByPhone(@RequestParam("phone") String phone, @RequestParam("captcha") String captcha) {
        TokenResponse token = authService.loginByPhone(phone, captcha);
        return Result.success(token);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/getUserInfo")
    public Result<UserInfoDTO> getUserInfo() {
        UserInfoDTO userInfo = authService.getCurrentUser();
        return Result.success(userInfo);
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refreshToken")
    public Result<String> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        // Sa-Token 提供了 token 续期的能力，这里简化处理
        // 实际项目中应该使用 refreshToken 查询数据库中的有效 token
        StpUtil.renewTimeout(refreshToken, 2592000);
        return Result.success(refreshToken);
    }
}
