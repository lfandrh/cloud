package com.jun.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.common.exception.BusinessException;
import com.jun.user.dto.RoleDTO;
import com.jun.user.dto.UserOperateRequest;
import com.jun.user.dto.UserSearchParams;
import com.jun.user.entity.Role;
import com.jun.user.entity.User;
import com.jun.user.entity.UserRole;
import com.jun.user.mapper.RoleMapper;
import com.jun.user.mapper.UserMapper;
import com.jun.user.mapper.UserRoleMapper;
import com.jun.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public IPage<User> getUserList(UserSearchParams params) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(params.getUserName() != null, User::getUserName, params.getUserName())
                .like(params.getNickName() != null, User::getNickName, params.getNickName())
                .like(params.getUserPhone() != null, User::getPhone, params.getUserPhone())
                .like(params.getUserEmail() != null, User::getEmail, params.getUserEmail())
                .eq(params.getUserGender() != null, User::getGender, params.getUserGender())
                .eq(params.getStatus() != null, User::getStatus, params.getStatus())
                .orderByDesc(User::getCreateTime);
        
        Page<User> page = new Page<>(params.getCurrent(), params.getSize());
        return page(page, wrapper);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .eq(Role::getStatus, 1));
        
        List<RoleDTO> result = new ArrayList<>();
        for (Role role : roles) {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setRoleName(role.getRoleName());
            dto.setRoleCode(role.getRoleCode());
            dto.setRoleDesc(role.getRoleDesc());
            dto.setStatus(role.getStatus());
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public void addUser(UserOperateRequest request) {
        // 检查用户名是否存在
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, request.getUserName()));
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setNickName(request.getNickName());
        user.setGender(request.getUserGender());
        user.setPhone(request.getUserPhone());
        user.setEmail(request.getUserEmail());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setPassword(request.getPassword() != null ? request.getPassword() : "123456");
        user.setCreateTime(LocalDateTime.now());

        save(user);

        // 保存用户角色关联
        saveUserRoles(user.getId(), request.getUserRoles());
    }

    @Override
    @Transactional
    public void updateUser(UserOperateRequest request) {
        User user = getById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否重复
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, request.getUserName()));
        if (existUser != null && !existUser.getId().equals(request.getId())) {
            throw new BusinessException("用户名已存在");
        }

        user.setUserName(request.getUserName());
        user.setNickName(request.getNickName());
        user.setGender(request.getUserGender());
        user.setPhone(request.getUserPhone());
        user.setEmail(request.getUserEmail());
        user.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        user.setUpdateTime(LocalDateTime.now());

        updateById(user);

        // 更新用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, request.getId()));
        saveUserRoles(request.getId(), request.getUserRoles());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (getById(id) == null) {
            throw new BusinessException("用户不存在");
        }
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, id));
        removeById(id);
    }

    @Override
    @Transactional
    public void batchDeleteUser(List<Long> ids) {
        for (Long id : ids) {
            deleteUser(id);
        }
    }

    @Override
    public User getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        
        return roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
    }

    private void saveUserRoles(Long userId, List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return;
        }

        List<Role> roles = roleMapper.selectList(null);
        for (String roleCode : roleCodes) {
            for (Role role : roles) {
                if (role.getRoleCode().equals(roleCode)) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(role.getId());
                    userRoleMapper.insert(userRole);
                    break;
                }
            }
        }
    }
}
