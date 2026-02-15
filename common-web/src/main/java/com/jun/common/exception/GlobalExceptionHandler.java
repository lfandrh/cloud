package com.jun.common.exception;

import lombok.extern.slf4j.Slf4j;
import com.jun.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        //打印完整堆栈日志（方便排查）
        log.error("系统异常", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        //打印完整堆栈日志（方便排查）
        log.error("系统异常", e);
        return Result.error("系统异常: " + e.getMessage());
    }
}
