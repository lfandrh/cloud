package com.jun.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jun.auth.dto.TokenResponse;
import com.jun.auth.dto.UserInfoDTO;
import com.jun.auth.entity.User;
import com.jun.auth.feign.UserFeignClient;
import com.jun.auth.mapper.UserMapper;
import com.jun.auth.service.AuthService;
import com.jun.common.context.UserContext;
import com.jun.common.exception.BusinessException;
import com.jun.common.result.Result;
import com.jun.common.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserFeignClient userFeignClient;

    @Override
    public TokenResponse login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, username));
        if (user == null) {
            throw new BusinessException("User not found");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("User is disabled");
        }
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        // Upgrade legacy plain-text password after successful login
        if (user.getPassword() != null && !user.getPassword().startsWith("{sha256}")) {
            user.setPassword(PasswordUtil.encode(password));
            userMapper.updateById(user);
        }

        StpUtil.login(user.getId());

        String token = StpUtil.getTokenValue();
        return TokenResponse.of(token, token);
    }

    @Override
    public TokenResponse loginByPhone(String phone, String captcha) {
        if (!StringUtils.hasText(captcha) || !"123456".equals(captcha.trim())) {
            throw new BusinessException("Invalid captcha");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new BusinessException("User not found");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("User is disabled");
        }

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        return TokenResponse.of(token, token);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("refreshToken required");
        }
        Object loginId = StpUtil.getLoginIdByToken(refreshToken);
        if (loginId == null) {
            throw new BusinessException("refreshToken invalid");
        }
        StpUtil.renewTimeout(refreshToken, 2592000);
        return TokenResponse.of(refreshToken, refreshToken);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserInfoDTO getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("User not logged in");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(String.valueOf(user.getId()));
        userInfoDTO.setUserName(user.getUserName());

        Result<List<String>> rolesResult = userFeignClient.getUserRoles(userId);
        List<String> roles = rolesResult != null && rolesResult.getData() != null
                ? rolesResult.getData()
                : Collections.emptyList();
        userInfoDTO.setRoles(roles);
        userInfoDTO.setButtons(Collections.emptyList());

        return userInfoDTO;
    }
}
