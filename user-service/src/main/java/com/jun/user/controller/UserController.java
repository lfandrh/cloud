package com.jun.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jun.common.context.UserContext;
import com.jun.common.exception.BusinessException;
import com.jun.common.result.Result;
import com.jun.user.dto.CurrentUserProfileDTO;
import com.jun.user.dto.MenuDTO;
import com.jun.user.dto.MenuOperateRequest;
import com.jun.user.dto.MenuTreeDTO;
import com.jun.user.dto.RoleDTO;
import com.jun.user.dto.RoleOperateRequest;
import com.jun.user.dto.UpdateCurrentUserPasswordRequest;
import com.jun.user.dto.UpdateCurrentUserProfileRequest;
import com.jun.user.dto.UserOperateRequest;
import com.jun.user.dto.UserSearchParams;
import com.jun.user.entity.User;
import com.jun.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getUserList")
    public Result<Map<String, Object>> getUserList(@ModelAttribute UserSearchParams params) {
        requireManagePermission();

        IPage<User> page = userService.getUserList(params);
        List<Map<String, Object>> records = new ArrayList<>();
        for (User user : page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("userName", safe(user.getUserName()));
            map.put("nickName", safe(user.getNickName()));
            map.put("userGender", user.getGender() == null ? "1" : String.valueOf(user.getGender()));
            map.put("userPhone", safe(user.getPhone()));
            map.put("userEmail", safe(user.getEmail()));
            map.put("status", user.getStatus() == null ? "1" : String.valueOf(user.getStatus()));
            map.put("userRoles", userService.getUserRoles(user.getId()));
            map.put("createTime", user.getCreateTime() == null ? "" : user.getCreateTime().toString());
            records.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/getRoleList")
    public Result<Map<String, Object>> getRoleList(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "roleCode", required = false) String roleCode,
            @RequestParam(value = "status", required = false) Integer status) {
        requireManagePermission();

        IPage<com.jun.user.entity.Role> page = userService.getRoleList(current, size, roleName, roleCode, status);
        List<RoleDTO> records = new ArrayList<>();
        for (com.jun.user.entity.Role role : page.getRecords()) {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setRoleName(role.getRoleName());
            dto.setRoleCode(role.getRoleCode());
            dto.setRoleDesc(role.getRoleDesc());
            dto.setStatus(role.getStatus());
            records.add(dto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/getAllRoles")
    public Result<List<RoleDTO>> getAllRoles() {
        requireManagePermission();
        return Result.success(userService.getAllRoles());
    }

    @PostMapping("/addRole")
    public Result<Void> addRole(@RequestBody RoleOperateRequest request) {
        requireManagePermission();
        userService.addRole(request);
        return Result.success();
    }

    @PostMapping("/updateRole")
    public Result<Void> updateRole(@RequestBody RoleOperateRequest request) {
        requireManagePermission();
        userService.updateRole(request);
        return Result.success();
    }

    @DeleteMapping("/deleteRole")
    public Result<Void> deleteRole(@RequestParam("id") Long id) {
        requireManagePermission();
        userService.deleteRole(id);
        return Result.success();
    }

    @DeleteMapping("/batchDeleteRole")
    public Result<Void> batchDeleteRole(@RequestParam("ids") String ids) {
        requireManagePermission();
        userService.batchDeleteRole(parseIds(ids));
        return Result.success();
    }

    @GetMapping("/getRoleMenuIds")
    public Result<List<Long>> getRoleMenuIds(@RequestParam("roleId") Long roleId) {
        requireManagePermission();
        return Result.success(userService.getRoleMenuIds(roleId));
    }

    @PostMapping("/updateRoleMenus")
    public Result<Void> updateRoleMenus(@RequestBody RoleOperateRequest request) {
        requireManagePermission();
        userService.updateRoleMenus(request.getId(), request.getMenuIds());
        return Result.success();
    }

    @PostMapping("/addUser")
    public Result<Void> addUser(@RequestBody UserOperateRequest request) {
        requireManagePermission();
        userService.addUser(request);
        return Result.success();
    }

    @PostMapping("/updateUser")
    public Result<Void> updateUser(@RequestBody UserOperateRequest request) {
        requireManagePermission();
        userService.updateUser(request);
        return Result.success();
    }

    @DeleteMapping("/deleteUser")
    public Result<Void> deleteUser(@RequestParam("id") Long id) {
        requireManagePermission();
        userService.deleteUser(id);
        return Result.success();
    }

    @DeleteMapping("/batchDeleteUser")
    public Result<Void> batchDeleteUser(@RequestParam("ids") String ids) {
        requireManagePermission();
        userService.batchDeleteUser(parseIds(ids));
        return Result.success();
    }

    @GetMapping("/getUserRoles")
    public Result<List<String>> getUserRoles(@RequestParam("userId") Long userId) {
        return Result.success(userService.getUserRoles(userId));
    }

    @GetMapping("/getMenuList/v2")
    public Result<Map<String, Object>> getMenuList(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        requireManagePermission();

        IPage<MenuDTO> page = userService.getMenuList(current, size);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/getMenuTreeList")
    public Result<List<MenuDTO>> getMenuTreeList() {
        requireManagePermission();
        return Result.success(userService.getMenuTreeList());
    }

    @GetMapping("/getAllPages")
    public Result<List<String>> getAllPages() {
        requireManagePermission();
        return Result.success(userService.getAllPages());
    }

    @GetMapping("/getMenuTree")
    public Result<List<MenuTreeDTO>> getMenuTree() {
        requireManagePermission();
        return Result.success(userService.getMenuTree());
    }

    @PostMapping("/addMenu")
    public Result<Void> addMenu(@RequestBody MenuOperateRequest request) {
        requireManagePermission();
        userService.addMenu(request);
        return Result.success();
    }

    @PostMapping("/updateMenu")
    public Result<Void> updateMenu(@RequestBody MenuOperateRequest request) {
        requireManagePermission();
        userService.updateMenu(request);
        return Result.success();
    }

    @DeleteMapping("/deleteMenu")
    public Result<Void> deleteMenu(@RequestParam("id") Long id) {
        requireManagePermission();
        userService.deleteMenu(id);
        return Result.success();
    }

    @DeleteMapping("/batchDeleteMenu")
    public Result<Void> batchDeleteMenu(@RequestParam("ids") String ids) {
        requireManagePermission();
        userService.batchDeleteMenu(parseIds(ids));
        return Result.success();
    }

    @GetMapping("/getCurrentUserProfile")
    public Result<CurrentUserProfileDTO> getCurrentUserProfile() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getCurrentUserProfile(userId));
    }

    @PostMapping("/updateCurrentUserProfile")
    public Result<Void> updateCurrentUserProfile(@RequestBody UpdateCurrentUserProfileRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateCurrentUserProfile(userId, request);
        return Result.success();
    }

    @PostMapping("/updateCurrentUserPassword")
    public Result<Void> updateCurrentUserPassword(@RequestBody UpdateCurrentUserPasswordRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateCurrentUserPassword(userId, request);
        return Result.success();
    }

    private void requireManagePermission() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("User not logged in");
        }

        List<String> roles = UserContext.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = userService.getUserRoles(userId);
        }

        if (!roles.contains("R_SUPER") && !roles.contains("R_ADMIN")) {
            throw new BusinessException("Permission denied");
        }
    }

    private List<Long> parseIds(String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            throw new BusinessException("ids is required");
        }
        String[] arr = ids.split(",");
        List<Long> result = new ArrayList<>();
        for (String item : arr) {
            String val = item == null ? "" : item.trim();
            if (val.isEmpty()) {
                continue;
            }
            try {
                result.add(Long.parseLong(val));
            } catch (NumberFormatException e) {
                throw new BusinessException("ids contains invalid number: " + val);
            }
        }
        if (result.isEmpty()) {
            throw new BusinessException("ids is required");
        }
        return result;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
