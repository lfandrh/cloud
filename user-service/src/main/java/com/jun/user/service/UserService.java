package com.jun.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jun.user.dto.MenuDTO;
import com.jun.user.dto.MenuOperateRequest;
import com.jun.user.dto.MenuTreeDTO;
import com.jun.user.dto.CurrentUserProfileDTO;
import com.jun.user.dto.RoleDTO;
import com.jun.user.dto.RoleOperateRequest;
import com.jun.user.dto.UpdateCurrentUserPasswordRequest;
import com.jun.user.dto.UpdateCurrentUserProfileRequest;
import com.jun.user.dto.UserOperateRequest;
import com.jun.user.dto.UserSearchParams;
import com.jun.user.entity.Role;
import com.jun.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    IPage<User> getUserList(UserSearchParams params);

    IPage<Role> getRoleList(Integer current, Integer size, String roleName, String roleCode, Integer status);

    List<RoleDTO> getAllRoles();

    void addUser(UserOperateRequest request);

    void updateUser(UserOperateRequest request);

    void deleteUser(Long id);

    void batchDeleteUser(List<Long> ids);

    User getUserById(Long id);

    List<String> getUserRoles(Long userId);

    void addRole(RoleOperateRequest request);

    void updateRole(RoleOperateRequest request);

    void deleteRole(Long roleId);

    void batchDeleteRole(List<Long> roleIds);

    List<Long> getRoleMenuIds(Long roleId);

    void updateRoleMenus(Long roleId, List<Long> menuIds);

    IPage<MenuDTO> getMenuList(Integer current, Integer size);

    List<MenuDTO> getMenuTreeList();

    List<String> getAllPages();

    List<MenuTreeDTO> getMenuTree();

    void addMenu(MenuOperateRequest request);

    void updateMenu(MenuOperateRequest request);

    void deleteMenu(Long id);

    void batchDeleteMenu(List<Long> ids);

    CurrentUserProfileDTO getCurrentUserProfile(Long userId);

    void updateCurrentUserProfile(Long userId, UpdateCurrentUserProfileRequest request);

    void updateCurrentUserPassword(Long userId, UpdateCurrentUserPasswordRequest request);
}
