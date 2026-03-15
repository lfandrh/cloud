package com.jun.auth.feign;

import com.jun.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", path = "/user")
public interface UserFeignClient {

    @GetMapping("/getUserRoles")
    Result<List<String>> getUserRoles(@RequestParam("userId") Long userId);

    @GetMapping("/getUserButtons")
    Result<List<String>> getUserButtons(@RequestParam("userId") Long userId);
}
