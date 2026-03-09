package com.jun.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jun.user.entity.Menu;
import com.jun.user.entity.Role;
import com.jun.user.entity.RoleMenu;
import com.jun.user.entity.UserRole;
import com.jun.user.mapper.MenuMapper;
import com.jun.user.mapper.RoleMapper;
import com.jun.user.mapper.RoleMenuMapper;
import com.jun.user.mapper.UserRoleMapper;
import com.jun.user.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @Override
    public List<Map<String, Object>> getConstantRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();

        routes.add(constantRoute("403", "/403", "layout.blank$view.403", false, true));
        routes.add(constantRoute("404", "/404", "layout.blank$view.404", false, true));
        routes.add(constantRoute("500", "/500", "layout.blank$view.500", false, true));
        routes.add(constantRoute(
                "iframe-page",
                "/iframe-page/:url",
                "layout.base$view.iframe-page",
                true,
                true
        ));
        routes.add(constantRoute(
                "login",
                "/login/:module(pwd-login|code-login|register|reset-pwd|bind-wechat)?",
                "layout.blank$view.login",
                false,
                true
        ));

        return routes;
    }

    @Override
    public Map<String, Object> getUserRoutes(Long userId) {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 1)
                .orderByAsc(Menu::getParentId)
                .orderByAsc(Menu::getOrderNum)
                .orderByAsc(Menu::getId));

        if (menus.isEmpty()) {
            return buildUserRouteResult(Collections.emptyList(), "home");
        }

        Map<Long, List<String>> menuRoles = buildMenuRolesMap();
        Set<String> userRoles = getUserRoleCodes(userId);
        boolean isSuperRole = userRoles.contains("R_SUPER");

        Map<Long, RouteNode> nodeMap = new LinkedHashMap<>();
        for (Menu menu : menus) {
            RouteNode node = new RouteNode();
            node.id = menu.getId();
            node.parentId = menu.getParentId() == null ? 0L : menu.getParentId();
            node.name = menu.getRouteName();
            node.path = menu.getRoutePath();
            node.component = menu.getComponent();
            node.menuName = menu.getMenuName();
            node.menuType = menu.getMenuType();
            node.icon = menu.getIcon();
            node.iconType = menu.getIconType();
            node.order = menu.getOrderNum() == null ? 0 : menu.getOrderNum();
            node.hideInMenu = menu.getHideInMenu() != null && menu.getHideInMenu() == 1;
            node.activeMenu = menu.getActiveMenu();
            node.roles = menuRoles.getOrDefault(menu.getId(), Collections.emptyList());
            nodeMap.put(menu.getId(), node);
        }

        List<RouteNode> roots = new ArrayList<>();
        for (RouteNode node : nodeMap.values()) {
            if (node.parentId == 0L || !nodeMap.containsKey(node.parentId)) {
                roots.add(node);
            } else {
                nodeMap.get(node.parentId).children.add(node);
            }
        }

        sortNodes(roots);
        List<RouteNode> filteredRoots = isSuperRole ? roots : filterByUserRoles(roots, userRoles);
        List<Map<String, Object>> routes = filteredRoots.stream()
                .map(this::toRouteMap)
                .collect(Collectors.toList());

        String home = findHomeRouteName(filteredRoots);
        return buildUserRouteResult(routes, home);
    }

    @Override
    public boolean isRouteExist(String routeName) {
        if (!StringUtils.hasText(routeName)) {
            return false;
        }
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 1)
                .eq(Menu::getRouteName, routeName));
        return count != null && count > 0;
    }

    private Map<Long, List<String>> buildMenuRolesMap() {
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(null);
        if (roleMenus.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> roleIdCodeMap = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                        .select(Role::getId, Role::getRoleCode))
                .stream()
                .collect(Collectors.toMap(Role::getId, Role::getRoleCode, (a, b) -> a));

        Map<Long, Set<String>> menuRoleSet = new LinkedHashMap<>();
        for (RoleMenu roleMenu : roleMenus) {
            String roleCode = roleIdCodeMap.get(roleMenu.getRoleId());
            if (!StringUtils.hasText(roleCode)) {
                continue;
            }
            menuRoleSet.computeIfAbsent(roleMenu.getMenuId(), k -> new LinkedHashSet<>()).add(roleCode);
        }

        Map<Long, List<String>> menuRoles = new LinkedHashMap<>();
        for (Map.Entry<Long, Set<String>> entry : menuRoleSet.entrySet()) {
            menuRoles.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return menuRoles;
    }

    private Set<String> getUserRoleCodes(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptySet();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        return roleMapper.selectBatchIds(roleIds).stream()
                .map(Role::getRoleCode)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void sortNodes(List<RouteNode> nodes) {
        nodes.sort(Comparator.comparingInt((RouteNode n) -> n.order).thenComparing(n -> n.id));
        for (RouteNode node : nodes) {
            if (!node.children.isEmpty()) {
                sortNodes(node.children);
            }
        }
    }

    private List<RouteNode> filterByUserRoles(List<RouteNode> nodes, Set<String> userRoles) {
        List<RouteNode> result = new ArrayList<>();
        for (RouteNode node : nodes) {
            List<RouteNode> childFiltered = filterByUserRoles(node.children, userRoles);
            boolean selfAllowed = node.roles.isEmpty() || node.roles.stream().anyMatch(userRoles::contains);

            if (selfAllowed || !childFiltered.isEmpty()) {
                RouteNode copy = node.copyWithoutChildren();
                copy.children = childFiltered;
                if (!selfAllowed && !copy.children.isEmpty()) {
                    copy.roles = Collections.emptyList();
                }
                result.add(copy);
            }
        }
        return result;
    }

    private Map<String, Object> toRouteMap(RouteNode node) {
        Map<String, Object> route = new LinkedHashMap<>();
        route.put("id", String.valueOf(node.id));
        route.put("name", node.name);
        route.put("path", node.path);
        if (StringUtils.hasText(node.component)) {
            route.put("component", node.component);
        }

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("title", StringUtils.hasText(node.name) ? node.name : node.menuName);
        if (StringUtils.hasText(node.name)) {
            meta.put("i18nKey", "route." + node.name);
        }
        meta.put("order", node.order);
        meta.put("keepAlive", true);
        meta.put("hideInMenu", node.hideInMenu);
        if (StringUtils.hasText(node.activeMenu)) {
            meta.put("activeMenu", node.activeMenu);
        }

        if (!node.roles.isEmpty()) {
            meta.put("roles", node.roles);
        }
        if (String.valueOf(node.iconType).equals("2")) {
            if (StringUtils.hasText(node.icon)) {
                meta.put("localIcon", node.icon);
            }
        } else {
            if (StringUtils.hasText(node.icon)) {
                meta.put("icon", node.icon);
            }
        }
        route.put("meta", meta);

        if (!node.children.isEmpty()) {
            List<Map<String, Object>> children = node.children.stream()
                    .map(this::toRouteMap)
                    .collect(Collectors.toList());
            route.put("children", children);
        }

        return route;
    }

    private String findHomeRouteName(List<RouteNode> nodes) {
        for (RouteNode node : nodes) {
            String fromChildren = findHomeRouteName(node.children);
            if (StringUtils.hasText(fromChildren)) {
                return fromChildren;
            }
            if (StringUtils.hasText(node.name) && StringUtils.hasText(node.component) && node.component.contains("view.")) {
                return node.name;
            }
        }
        return "home";
    }

    private Map<String, Object> buildUserRouteResult(List<Map<String, Object>> routes, String home) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("routes", routes);
        result.put("home", home);
        return result;
    }

    private Map<String, Object> constantRoute(String name, String path, String component, boolean keepAlive, boolean hideInMenu) {
        Map<String, Object> route = new LinkedHashMap<>();
        route.put("id", name);
        route.put("name", name);
        route.put("path", path);
        route.put("component", component);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("title", name);
        meta.put("i18nKey", "route." + name);
        meta.put("constant", true);
        meta.put("hideInMenu", hideInMenu);
        if (keepAlive) {
            meta.put("keepAlive", true);
        }
        route.put("meta", meta);
        return route;
    }

    private static final class RouteNode {
        private Long id;
        private Long parentId;
        private String name;
        private String path;
        private String component;
        private String menuName;
        private Integer menuType;
        private String icon;
        private Integer iconType;
        private Integer order;
        private boolean hideInMenu;
        private String activeMenu;
        private List<String> roles = Collections.emptyList();
        private List<RouteNode> children = new ArrayList<>();

        private RouteNode copyWithoutChildren() {
            RouteNode copy = new RouteNode();
            copy.id = this.id;
            copy.parentId = this.parentId;
            copy.name = this.name;
            copy.path = this.path;
            copy.component = this.component;
            copy.menuName = this.menuName;
            copy.menuType = this.menuType;
            copy.icon = this.icon;
            copy.iconType = this.iconType;
            copy.order = this.order;
            copy.hideInMenu = this.hideInMenu;
            copy.activeMenu = this.activeMenu;
            copy.roles = this.roles;
            return copy;
        }
    }
}
