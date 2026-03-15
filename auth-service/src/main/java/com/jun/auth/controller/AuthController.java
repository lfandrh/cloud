package com.jun.auth.controller;

import com.jun.auth.dto.TokenResponse;
import com.jun.auth.dto.UserInfoDTO;
import com.jun.auth.service.AuthService;
import com.jun.common.result.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestParam("userName") @NotBlank String userName,
                                       @RequestParam("password") @NotBlank String password) {
        log.info("auth_audit action=login userName={}", userName);
        return Result.success(authService.login(userName, password));
    }

    @PostMapping("/loginByPhone")
    public Result<TokenResponse> loginByPhone(@RequestParam("phone") @NotBlank String phone,
                                              @RequestParam("captcha") @NotBlank String captcha) {
        log.info("auth_audit action=login_by_phone phone={}", phone);
        return Result.success(authService.loginByPhone(phone, captcha));
    }

    @PostMapping("/sendCaptcha")
    public Result<Void> sendCaptcha(@RequestParam("phone") @NotBlank String phone) {
        log.info("auth_audit action=send_captcha phone={}", phone);
        authService.sendCaptcha(phone);
        return Result.success();
    }

    @PostMapping("/verifyCaptcha")
    public Result<Boolean> verifyCaptcha(@RequestParam("phone") @NotBlank String phone,
                                         @RequestParam("captcha") @NotBlank String captcha) {
        return Result.success(authService.verifyCaptcha(phone, captcha));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("auth_audit action=logout");
        authService.logout();
        return Result.success();
    }

    @GetMapping("/getUserInfo")
    public Result<UserInfoDTO> getUserInfo() {
        return Result.success(authService.getCurrentUser());
    }

    @PostMapping("/refreshToken")
    public Result<TokenResponse> refreshToken(@RequestParam("refreshToken") @NotBlank String refreshToken) {
        log.info("auth_audit action=refresh_token");
        return Result.success(authService.refreshToken(refreshToken));
    }
}
