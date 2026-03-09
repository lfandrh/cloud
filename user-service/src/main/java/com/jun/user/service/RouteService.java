package com.jun.user.service;

import java.util.List;
import java.util.Map;

public interface RouteService {

    List<Map<String, Object>> getConstantRoutes();

    Map<String, Object> getUserRoutes(Long userId);

    boolean isRouteExist(String routeName);
}
