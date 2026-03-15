package com.jun.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jun.common.context.UserContext;
import com.jun.common.enums.AppErrorCode;
import com.jun.common.exception.BusinessException;
import com.jun.common.result.Result;
import com.jun.user.dto.CurrentUserProfileDTO;
import com.jun.user.dto.ButtonTreeNodeDTO;
import com.jun.user.dto.RoleButtonAuthRequest;
import com.jun.user.dto.RoleMenuAuthRequest;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/getUserList")
    public Result<Map<String, Object>> getUserList(@Valid @ModelAttribute UserSearchParams params) {
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
            @RequestParam(value = "current", defaultValue = "1") @Min(1) Integer current,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
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
    public Result<Void> addRole(@Valid @RequestBody RoleOperateRequest request) {
        requireManagePermission();
        log.info("biz_audit action=add_role roleCode={}", request.getRoleCode());
        userService.addRole(request);
        return Result.success();
    }

    @PostMapping("/updateRole")
    public Result<Void> updateRole(@Valid @RequestBody RoleOperateRequest request) {
        requireManagePermission();
        log.info("biz_audit action=update_role roleId={}", request.getId());
        userService.updateRole(request);
        return Result.success();
    }

    @DeleteMapping("/deleteRole")
    public Result<Void> deleteRole(@RequestParam("id") @Min(1) Long id) {
        requireManagePermission();
        log.info("biz_audit action=delete_role roleId={}", id);
        userService.deleteRole(id);
        return Result.success();
    }

    @DeleteMapping("/batchDeleteRole")
    public Result<Void> batchDeleteRole(@RequestParam("ids") @NotBlank String ids) {
        requireManagePermission();
        userService.batchDeleteRole(parseIds(ids));
        return Result.success();
    }

    @GetMapping("/getRoleMenuIds")
    public Result<List<Long>> getRoleMenuIds(@RequestParam("roleId") @Min(1) Long roleId) {
        requireManagePermission();
        return Result.success(userService.getRoleMenuIds(roleId));
    }

    @PostMapping("/updateRoleMenus")
    public Result<Void> updateRoleMenus(@Valid @RequestBody RoleMenuAuthRequest request) {
        requireManagePermission();
        userService.updateRoleMenus(request.getId(), request.getMenuIds());
        return Result.success();
    }

    @GetMapping("/getButtonTree")
    public Result<List<ButtonTreeNodeDTO>> getButtonTree() {
        requireManagePermission();
        return Result.success(userService.getButtonTree());
    }

    @GetMapping("/getRoleButtonIds")
    public Result<List<Long>> getRoleButtonIds(@RequestParam("roleId") @Min(1) Long roleId) {
        requireManagePermission();
        return Result.success(userService.getRoleButtonIds(roleId));
    }

    @PostMapping("/updateRoleButtons")
    public Result<Void> updateRoleButtons(@Valid @RequestBody RoleButtonAuthRequest request) {
        requireManagePermission();
        userService.updateRoleButtons(request.getId(), request.getButtonIds());
        return Result.success();
    }

    @PostMapping("/addUser")
    public Result<Void> addUser(@Valid @RequestBody UserOperateRequest request) {
        requireManagePermission();
        log.info("biz_audit action=add_user userName={}", request.getUserName());
        userService.addUser(request);
        return Result.success();
    }

    @PostMapping("/updateUser")
    public Result<Void> updateUser(@Valid @RequestBody UserOperateRequest request) {
        requireManagePermission();
        log.info("biz_audit action=update_user userId={}", request.getId());
        userService.updateUser(request);
        return Result.success();
    }

    @DeleteMapping("/deleteUser")
    public Result<Void> deleteUser(@RequestParam("id") @Min(1) Long id) {
        requireManagePermission();
        log.info("biz_audit action=delete_user userId={}", id);
        userService.deleteUser(id);
        return Result.success();
    }

    @DeleteMapping("/batchDeleteUser")
    public Result<Void> batchDeleteUser(@RequestParam("ids") @NotBlank String ids) {
        requireManagePermission();
        userService.batchDeleteUser(parseIds(ids));
        return Result.success();
    }

    @GetMapping("/getUserRoles")
    public Result<List<String>> getUserRoles(@RequestParam("userId") @Min(1) Long userId) {
        return Result.success(userService.getUserRoles(userId));
    }

    @GetMapping("/getUserButtons")
    public Result<List<String>> getUserButtons(@RequestParam("userId") @Min(1) Long userId) {
        return Result.success(userService.getUserButtons(userId));
    }

    @GetMapping("/getMenuList/v2")
    public Result<Map<String, Object>> getMenuList(
            @RequestParam(value = "current", defaultValue = "1") @Min(1) Integer current,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
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
    public Result<Void> addMenu(@Valid @RequestBody MenuOperateRequest request) {
        requireManagePermission();
        userService.addMenu(request);
        return Result.success();
    }

    @PostMapping("/updateMenu")
    public Result<Void> updateMenu(@Valid @RequestBody MenuOperateRequest request) {
        requireManagePermission();
        userService.updateMenu(request);
        return Result.success();
    }

    @DeleteMapping("/deleteMenu")
    public Result<Void> deleteMenu(@RequestParam("id") @Min(1) Long id) {
        requireManagePermission();
        userService.deleteMenu(id);
        return Result.success();
    }

    @DeleteMapping("/batchDeleteMenu")
    public Result<Void> batchDeleteMenu(@RequestParam("ids") @NotBlank String ids) {
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
    public Result<Void> updateCurrentUserProfile(@Valid @RequestBody UpdateCurrentUserProfileRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateCurrentUserProfile(userId, request);
        return Result.success();
    }

    @PostMapping("/updateCurrentUserPassword")
    public Result<Void> updateCurrentUserPassword(@Valid @RequestBody UpdateCurrentUserPasswordRequest request) {
        Long userId = UserContext.getUserId();
        log.info("biz_audit action=change_password userId={}", userId);
        userService.updateCurrentUserPassword(userId, request);
        return Result.success();
    }

    private void requireManagePermission() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(AppErrorCode.USER_NOT_LOGGED_IN);
        }

        List<String> roles = UserContext.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = userService.getUserRoles(userId);
        }

        if (!roles.contains("R_SUPER") && !roles.contains("R_ADMIN")) {
            throw new BusinessException(AppErrorCode.PERMISSION_DENIED);
        }
    }

    private List<Long> parseIds(String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "ids is required");
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
                throw new BusinessException(AppErrorCode.BAD_REQUEST, "ids contains invalid number: " + val);
            }
        }
        if (result.isEmpty()) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "ids is required");
        }
        return result;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
