package com.jun.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jun.user.dto.RoleDTO;
import com.jun.user.dto.UserOperateRequest;
import com.jun.user.dto.UserSearchParams;
import com.jun.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     */
    IPage<User> getUserList(UserSearchParams params);

    /**
     * 获取所有启用的角色
     */
    List<RoleDTO> getAllRoles();

    /**
     * 新增用户
     */
    void addUser(UserOperateRequest request);

    /**
     * 更新用户
     */
    void updateUser(UserOperateRequest request);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     */
    void batchDeleteUser(List<Long> ids);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);
}
