package com.jun.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jun.common.result.Result;
import com.jun.user.dto.RoleDTO;
import com.jun.user.dto.UserOperateRequest;
import com.jun.user.dto.UserSearchParams;
import com.jun.user.entity.User;
import com.jun.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/systemManage")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/getUserList")
    public Result<Map<String, Object>> getUserList(@ModelAttribute UserSearchParams params) {
        IPage<User> page = userService.getUserList(params);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> records = new ArrayList<>();
        for (User user : page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("userName", user.getUserName() != null ? user.getUserName() : "");
            map.put("nickName", user.getNickName() != null ? user.getNickName() : "");
            map.put("userGender", user.getGender() != null ? user.getGender() : 1);
            map.put("userPhone", user.getPhone() != null ? user.getPhone() : "");
            map.put("userEmail", user.getEmail() != null ? user.getEmail() : "");
            map.put("status", user.getStatus() != null ? user.getStatus() : 1);
            map.put("createTime", user.getCreateTime() != null ? user.getCreateTime().toString() : "");
            records.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        
        return Result.success(result);
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/getAllRoles")
    public Result<List<RoleDTO>> getAllRoles() {
        return Result.success(userService.getAllRoles());
    }

    /**
     * 新增用户
     */
    @PostMapping("/addUser")
    public Result<Void> addUser(@RequestBody UserOperateRequest request) {
        userService.addUser(request);
        return Result.success();
    }

    /**
     * 更新用户
     */
    @PostMapping("/updateUser")
    public Result<Void> updateUser(@RequestBody UserOperateRequest request) {
        userService.updateUser(request);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/deleteUser")
    public Result<Void> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batchDeleteUser")
    public Result<Void> batchDeleteUser(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
        userService.batchDeleteUser(idList);
        return Result.success();
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/getUserRoles")
    public Result<List<String>> getUserRoles(@RequestParam("userId") Long userId) {
        return Result.success(userService.getUserRoles(userId));
    }
}
