package com.jun.auth.controller;

import com.jun.auth.dto.TokenResponse;
import com.jun.auth.dto.UserInfoDTO;
import com.jun.auth.service.AuthService;
import com.jun.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        return Result.success(authService.login(userName, password));
    }

    @PostMapping("/loginByPhone")
    public Result<TokenResponse> loginByPhone(@RequestParam("phone") String phone, @RequestParam("captcha") String captcha) {
        return Result.success(authService.loginByPhone(phone, captcha));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/getUserInfo")
    public Result<UserInfoDTO> getUserInfo() {
        return Result.success(authService.getCurrentUser());
    }

    @PostMapping("/refreshToken")
    public Result<TokenResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return Result.success(authService.refreshToken(refreshToken));
    }
}
