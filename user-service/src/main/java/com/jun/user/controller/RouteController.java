package com.jun.user.controller;

import com.jun.common.context.UserContext;
import com.jun.common.result.Result;
import com.jun.user.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/getConstantRoutes")
    public Result<List<Map<String, Object>>> getConstantRoutes() {
        return Result.success(routeService.getConstantRoutes());
    }

    @GetMapping("/getUserRoutes")
    public Result<Map<String, Object>> getUserRoutes() {
        Long userId = UserContext.getUserId();
        return Result.success(routeService.getUserRoutes(userId));
    }

    @GetMapping("/isRouteExist")
    public Result<Boolean> isRouteExist(@RequestParam("routeName") String routeName) {
        return Result.success(routeService.isRouteExist(routeName));
    }
}
