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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserFeignClient userFeignClient;

    @Override
    public TokenResponse login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, username));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        if (!password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        StpUtil.login(user.getId());
        
        String token = StpUtil.getTokenValue();
        String refreshToken = "refresh_" + System.currentTimeMillis() + "_" + user.getId();
        
        return TokenResponse.of(token, refreshToken);
    }

    @Override
    public TokenResponse loginByPhone(String phone, String captcha) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        StpUtil.login(user.getId());
        
        String token = StpUtil.getTokenValue();
        String refreshToken = "refresh_" + System.currentTimeMillis() + "_" + user.getId();
        
        return TokenResponse.of(token, refreshToken);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserInfoDTO getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
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
