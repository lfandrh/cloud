package com.jun.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jun.auth.dto.TokenResponse;
import com.jun.auth.dto.UserInfoDTO;
import com.jun.auth.entity.User;
import com.jun.auth.feign.UserFeignClient;
import com.jun.auth.mapper.UserMapper;
import com.jun.common.enums.AppErrorCode;
import com.jun.auth.service.AuthService;
import com.jun.common.context.UserContext;
import com.jun.common.exception.BusinessException;
import com.jun.common.result.Result;
import com.jun.common.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String CAPTCHA_KEY_PREFIX = "auth:captcha:";
    private static final String CAPTCHA_FAIL_PREFIX = "auth:captcha:fail:";
    private static final String CAPTCHA_SEND_LIMIT_PREFIX = "auth:captcha:send:";
    private static final int CAPTCHA_TTL_SECONDS = 300;
    private static final int CAPTCHA_FAIL_TTL_SECONDS = 600;
    private static final int CAPTCHA_FAIL_LIMIT = 5;
    private static final int CAPTCHA_SEND_INTERVAL_SECONDS = 60;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:token:";
    private static final String USER_REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:user:";
    private static final long REFRESH_TOKEN_TTL_SECONDS = 2592000L;

    private final UserMapper userMapper;
    private final UserFeignClient userFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public TokenResponse login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, username));
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(AppErrorCode.INVALID_CREDENTIALS);
        }
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException(AppErrorCode.INVALID_CREDENTIALS);
        }

        if (PasswordUtil.needsUpgrade(user.getPassword())) {
            user.setPassword(PasswordUtil.encode(password));
            userMapper.updateById(user);
        }

        StpUtil.login(user.getId());
        String accessToken = StpUtil.getTokenValue();
        String refreshToken = issueRefreshToken(user.getId());
        return TokenResponse.of(accessToken, refreshToken);
    }

    @Override
    public TokenResponse loginByPhone(String phone, String captcha) {
        String failCountKey = CAPTCHA_FAIL_PREFIX + phone;
        int failCount = parseInt(redisTemplate.opsForValue().get(failCountKey));
        if (failCount >= CAPTCHA_FAIL_LIMIT) {
            throw new BusinessException(AppErrorCode.TOO_MANY_REQUESTS, "Too many attempts, please try later");
        }

        if (!verifyCaptcha(phone, captcha)) {
            long count = redisTemplate.opsForValue().increment(failCountKey);
            if (count == 1) {
                redisTemplate.expire(failCountKey, Duration.ofSeconds(CAPTCHA_FAIL_TTL_SECONDS));
            }
            throw new BusinessException(AppErrorCode.INVALID_CAPTCHA);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(AppErrorCode.INVALID_CREDENTIALS);
        }

        StpUtil.login(user.getId());
        String accessToken = StpUtil.getTokenValue();
        String refreshToken = issueRefreshToken(user.getId());

        redisTemplate.delete(failCountKey);
        redisTemplate.delete(CAPTCHA_KEY_PREFIX + phone);
        return TokenResponse.of(accessToken, refreshToken);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(AppErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshTokenRecord record = loadRefreshToken(refreshToken);
        if (record == null || !record.isActive() || record.getExpireAtEpochSecond() <= Instant.now().getEpochSecond()) {
            throw new BusinessException(AppErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userRefreshKey = USER_REFRESH_TOKEN_KEY_PREFIX + record.getUserId();
        String latestRefreshToken = redisTemplate.opsForValue().get(userRefreshKey);
        if (!refreshToken.equals(latestRefreshToken)) {
            throw new BusinessException(AppErrorCode.INVALID_REFRESH_TOKEN);
        }

        StpUtil.login(record.getUserId());
        String accessToken = StpUtil.getTokenValue();

        revokeRefreshToken(refreshToken, record.getUserId());
        String newRefreshToken = issueRefreshToken(record.getUserId());
        return TokenResponse.of(accessToken, newRefreshToken);
    }

    @Override
    public void sendCaptcha(String phone) {
        String sendLimitKey = CAPTCHA_SEND_LIMIT_PREFIX + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(sendLimitKey))) {
            throw new BusinessException(AppErrorCode.TOO_MANY_REQUESTS, "Captcha sent too frequently");
        }

        String captcha = generateCaptcha();
        redisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + phone, captcha, Duration.ofSeconds(CAPTCHA_TTL_SECONDS));
        redisTemplate.opsForValue().set(sendLimitKey, "1", Duration.ofSeconds(CAPTCHA_SEND_INTERVAL_SECONDS));

        // TODO: replace with real SMS provider integration.
        log.info("Send captcha for phone={}, code={}", phone, captcha);
    }

    @Override
    public boolean verifyCaptcha(String phone, String captcha) {
        String storedCaptcha = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + phone);
        if (!StringUtils.hasText(storedCaptcha) || !StringUtils.hasText(captcha)) {
            return false;
        }
        return storedCaptcha.equals(captcha.trim());
    }

    @Override
    public void logout() {
        Long userId = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsLong();
        }
        StpUtil.logout();
        if (userId != null) {
            revokeUserRefreshToken(userId);
        }
    }

    @Override
    public UserInfoDTO getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(AppErrorCode.USER_NOT_LOGGED_IN);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User not found");
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(String.valueOf(user.getId()));
        userInfoDTO.setUserName(user.getUserName());

        Result<List<String>> rolesResult = userFeignClient.getUserRoles(userId);
        List<String> roles = rolesResult != null && rolesResult.getData() != null
                ? rolesResult.getData()
                : Collections.emptyList();
        userInfoDTO.setRoles(roles);
        Result<List<String>> buttonsResult = userFeignClient.getUserButtons(userId);
        List<String> buttons = buttonsResult != null && buttonsResult.getData() != null
                ? buttonsResult.getData()
                : Collections.emptyList();
        userInfoDTO.setButtons(buttons);

        return userInfoDTO;
    }

    private String issueRefreshToken(Long userId) {
        revokeUserRefreshToken(userId);

        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        long expireAtEpochSecond = Instant.now().plusSeconds(REFRESH_TOKEN_TTL_SECONDS).getEpochSecond();

        RefreshTokenRecord record = new RefreshTokenRecord(userId, tokenId, expireAtEpochSecond, 1);
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String userKey = USER_REFRESH_TOKEN_KEY_PREFIX + userId;

        redisTemplate.opsForValue().set(refreshKey, toJson(record), Duration.ofSeconds(REFRESH_TOKEN_TTL_SECONDS));
        redisTemplate.opsForValue().set(userKey, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_TTL_SECONDS));
        return refreshToken;
    }

    private void revokeUserRefreshToken(Long userId) {
        String userKey = USER_REFRESH_TOKEN_KEY_PREFIX + userId;
        String refreshToken = redisTemplate.opsForValue().get(userKey);
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }
        revokeRefreshToken(refreshToken, userId);
    }

    private void revokeRefreshToken(String refreshToken, Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + refreshToken);
        redisTemplate.delete(USER_REFRESH_TOKEN_KEY_PREFIX + userId);
    }

    private RefreshTokenRecord loadRefreshToken(String refreshToken) {
        String json = redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + refreshToken);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, RefreshTokenRecord.class);
        } catch (JsonProcessingException e) {
            log.warn("Parse refresh token record failed: {}", e.getMessage());
            return null;
        }
    }

    private String toJson(RefreshTokenRecord record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new BusinessException(AppErrorCode.SYSTEM_ERROR, "Failed to serialize refresh token");
        }
    }

    private String generateCaptcha() {
        int val = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(val);
    }

    private int parseInt(String val) {
        if (!StringUtils.hasText(val)) {
            return 0;
        }
        try {
            return Integer.parseInt(val);
        } catch (Exception ignored) {
            return 0;
        }
    }
}
