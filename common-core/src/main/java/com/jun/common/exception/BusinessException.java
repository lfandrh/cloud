package com.jun.common.exception;

import com.jun.common.enums.AppErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(AppErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(AppErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
