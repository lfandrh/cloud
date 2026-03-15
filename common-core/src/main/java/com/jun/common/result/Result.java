package com.jun.common.result;

import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;
    private Long timestamp;
    private String traceId;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        fillMeta(result);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        fillMeta(result);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    private static <T> void fillMeta(Result<T> result) {
        result.setTimestamp(Instant.now().toEpochMilli());
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        result.setTraceId(traceId);
    }
}
