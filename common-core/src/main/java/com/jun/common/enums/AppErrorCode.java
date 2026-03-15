package com.jun.common.enums;

import lombok.Getter;

@Getter
public enum AppErrorCode {
    BAD_REQUEST(4000, "Invalid request"),
    INVALID_CREDENTIALS(4001, "Invalid credentials"),
    INVALID_CAPTCHA(4002, "Invalid captcha"),
    INVALID_REFRESH_TOKEN(4003, "Invalid refresh token"),
    USER_NOT_LOGGED_IN(4010, "User not logged in"),
    PERMISSION_DENIED(4030, "Permission denied"),
    RESOURCE_NOT_FOUND(4040, "Resource not found"),
    RESOURCE_CONFLICT(4090, "Resource conflict"),
    TOO_MANY_REQUESTS(4290, "Too many requests"),
    SYSTEM_ERROR(5000, "System error");

    private final Integer code;
    private final String defaultMessage;

    AppErrorCode(Integer code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}

