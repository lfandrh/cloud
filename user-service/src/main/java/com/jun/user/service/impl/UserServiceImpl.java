package com.jun.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.common.enums.AppErrorCode;
import com.jun.common.exception.BusinessException;
import com.jun.common.security.PasswordUtil;
import com.jun.user.dto.MenuButtonDTO;
import com.jun.user.dto.ButtonTreeNodeDTO;
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
import com.jun.user.entity.Menu;
import com.jun.user.entity.MenuButton;
import com.jun.user.entity.Role;
import com.jun.user.entity.RoleButton;
import com.jun.user.entity.User;
import com.jun.user.entity.UserRole;
import com.jun.user.mapper.MenuButtonMapper;
import com.jun.user.mapper.MenuMapper;
import com.jun.user.mapper.RoleMapper;
import com.jun.user.mapper.RoleMenuMapper;
import com.jun.user.mapper.RoleButtonMapper;
import com.jun.user.mapper.UserMapper;
import com.jun.user.mapper.UserRoleMapper;
import com.jun.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleButtonMapper roleButtonMapper;
    private final UserRoleMapper userRoleMapper;
    private final MenuMapper menuMapper;
    private final MenuButtonMapper menuButtonMapper;

    @Override
    public IPage<User> getUserList(UserSearchParams params) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(params.getUserName()), User::getUserName, params.getUserName())
                .like(StringUtils.hasText(params.getNickName()), User::getNickName, params.getNickName())
                .like(StringUtils.hasText(params.getUserPhone()), User::getPhone, params.getUserPhone())
                .like(StringUtils.hasText(params.getUserEmail()), User::getEmail, params.getUserEmail())
                .eq(params.getUserGender() != null, User::getGender, params.getUserGender())
                .eq(params.getStatus() != null, User::getStatus, params.getStatus())
                .orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(params.getCurrent(), params.getSize());
        return page(page, wrapper);
    }

    @Override
    public IPage<Role> getRoleList(Integer current, Integer size, String roleName, String roleCode, Integer status) {
        Page<Role> page = new Page<>(current, size);
        return roleMapper.selectRolePage(page, roleName, roleCode, status);
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
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, request.getUserName()));
        if (existUser != null) {
            throw new BusinessException(AppErrorCode.RESOURCE_CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setNickName(request.getNickName());
        user.setGender(request.getUserGender());
        user.setPhone(request.getUserPhone());
        user.setEmail(request.getUserEmail());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setPassword(PasswordUtil.encode(request.getPassword() != null ? request.getPassword() : "123456"));
        user.setCreateTime(LocalDateTime.now());

        save(user);
        saveUserRoles(user.getId(), request.getUserRoles());
    }

    @Override
    @Transactional
    public void updateUser(UserOperateRequest request) {
        User user = getById(request.getId());
        if (user == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User does not exist");
        }

        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, request.getUserName()));
        if (existUser != null && !existUser.getId().equals(request.getId())) {
            throw new BusinessException(AppErrorCode.RESOURCE_CONFLICT, "Username already exists");
        }

        user.setUserName(request.getUserName());
        user.setNickName(request.getNickName());
        user.setGender(request.getUserGender());
        user.setPhone(request.getUserPhone());
        user.setEmail(request.getUserEmail());
        user.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encode(request.getPassword()));
        }
        user.setUpdateTime(LocalDateTime.now());

        updateById(user);

        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, request.getId()));
        saveUserRoles(request.getId(), request.getUserRoles());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User does not exist");
        }
        if ("admin".equalsIgnoreCase(user.getUserName())) {
            throw new BusinessException(AppErrorCode.PERMISSION_DENIED, "Default super admin cannot be deleted");
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
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User does not exist");
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

    @Override
    @Transactional
    public void addRole(RoleOperateRequest request) {
        validateRoleUnique(request.getRoleName(), request.getRoleCode(), null);

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setRoleDesc(request.getRoleDesc());
        role.setStatus(parseInteger(request.getStatus(), 1));
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void updateRole(RoleOperateRequest request) {
        if (request.getId() == null) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Role id is required");
        }

        Role role = roleMapper.selectById(request.getId());
        if (role == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "Role does not exist");
        }

        validateRoleUnique(request.getRoleName(), request.getRoleCode(), request.getId());

        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setRoleDesc(request.getRoleDesc());
        role.setStatus(parseInteger(request.getStatus(), role.getStatus() == null ? 1 : role.getStatus()));
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        if (roleId == null) {
            return;
        }
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "Role does not exist");
        }
        if (isProtectedRole(role)) {
            throw new BusinessException(AppErrorCode.PERMISSION_DENIED, "Built-in role cannot be deleted");
        }

        roleMenuMapper.delete(new LambdaQueryWrapper<com.jun.user.entity.RoleMenu>()
                .eq(com.jun.user.entity.RoleMenu::getRoleId, roleId));
        roleButtonMapper.delete(new LambdaQueryWrapper<RoleButton>()
                .eq(RoleButton::getRoleId, roleId));
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, roleId));
        roleMapper.deleteById(roleId);
    }

    @Override
    @Transactional
    public void batchDeleteRole(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            deleteRole(roleId);
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return roleMenuMapper.selectList(new LambdaQueryWrapper<com.jun.user.entity.RoleMenu>()
                        .eq(com.jun.user.entity.RoleMenu::getRoleId, roleId))
                .stream()
                .map(com.jun.user.entity.RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Role id is required");
        }
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "Role does not exist");
        }

        roleMenuMapper.delete(new LambdaQueryWrapper<com.jun.user.entity.RoleMenu>()
                .eq(com.jun.user.entity.RoleMenu::getRoleId, roleId));
        roleButtonMapper.delete(new LambdaQueryWrapper<RoleButton>()
                .eq(RoleButton::getRoleId, roleId));

        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }

        Set<Long> dedup = new LinkedHashSet<>(menuIds);
        for (Long menuId : dedup) {
            if (menuId == null) {
                continue;
            }
            com.jun.user.entity.RoleMenu roleMenu = new com.jun.user.entity.RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<ButtonTreeNodeDTO> getButtonTree() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 1)
                .eq(Menu::getMenuType, 2)
                .orderByAsc(Menu::getOrderNum)
                .orderByAsc(Menu::getId));
        if (menus.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> menuIds = menus.stream().map(Menu::getId).filter(Objects::nonNull).collect(Collectors.toList());
        List<MenuButton> buttons = menuButtonMapper.selectList(new LambdaQueryWrapper<MenuButton>()
                .eq(MenuButton::getStatus, 1)
                .in(MenuButton::getMenuId, menuIds)
                .orderByAsc(MenuButton::getId));
        Map<Long, List<MenuButton>> menuButtonMap = buttons.stream()
                .collect(Collectors.groupingBy(MenuButton::getMenuId, LinkedHashMap::new, Collectors.toList()));

        List<ButtonTreeNodeDTO> tree = new ArrayList<>();
        for (Menu menu : menus) {
            ButtonTreeNodeDTO menuNode = new ButtonTreeNodeDTO();
            menuNode.setKey("menu-" + menu.getId());
            menuNode.setLabel(StringUtils.hasText(menu.getMenuName()) ? menu.getMenuName() : "menu-" + menu.getId());
            menuNode.setDisabled(false);
            List<ButtonTreeNodeDTO> children = new ArrayList<>();

            for (MenuButton button : menuButtonMap.getOrDefault(menu.getId(), Collections.emptyList())) {
                ButtonTreeNodeDTO buttonNode = new ButtonTreeNodeDTO();
                buttonNode.setKey("btn-" + button.getId());
                buttonNode.setLabel(StringUtils.hasText(button.getButtonName()) ? button.getButtonName() : button.getButtonCode());
                buttonNode.setDisabled(false);
                children.add(buttonNode);
            }
            menuNode.setChildren(children);
            if (!children.isEmpty()) {
                tree.add(menuNode);
            }
        }
        return tree;
    }

    @Override
    public List<Long> getRoleButtonIds(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return roleButtonMapper.selectList(new LambdaQueryWrapper<RoleButton>()
                        .eq(RoleButton::getRoleId, roleId))
                .stream()
                .map(RoleButton::getButtonId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRoleButtons(Long roleId, List<Long> buttonIds) {
        if (roleId == null) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Role id is required");
        }
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "Role does not exist");
        }

        roleButtonMapper.delete(new LambdaQueryWrapper<RoleButton>().eq(RoleButton::getRoleId, roleId));
        if (buttonIds == null || buttonIds.isEmpty()) {
            return;
        }

        Set<Long> roleMenuIds = new LinkedHashSet<>(getRoleMenuIds(roleId));
        if (roleMenuIds.isEmpty()) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Please assign menu permissions before button permissions");
        }

        List<Long> dedupButtonIds = buttonIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (dedupButtonIds.isEmpty()) {
            return;
        }

        List<MenuButton> validButtons = menuButtonMapper.selectList(new LambdaQueryWrapper<MenuButton>()
                .in(MenuButton::getId, dedupButtonIds)
                .eq(MenuButton::getStatus, 1)
                .in(MenuButton::getMenuId, roleMenuIds));

        Set<Long> validButtonIds = validButtons.stream().map(MenuButton::getId).collect(Collectors.toSet());
        for (Long buttonId : dedupButtonIds) {
            if (!validButtonIds.contains(buttonId)) {
                throw new BusinessException(AppErrorCode.BAD_REQUEST, "Button id is invalid for this role: " + buttonId);
            }
        }

        for (Long buttonId : dedupButtonIds) {
            RoleButton roleButton = new RoleButton();
            roleButton.setRoleId(roleId);
            roleButton.setButtonId(buttonId);
            roleButtonMapper.insert(roleButton);
        }
    }

    @Override
    public List<String> getUserButtons(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<RoleButton> roleButtons = roleButtonMapper.selectList(new LambdaQueryWrapper<RoleButton>().in(RoleButton::getRoleId, roleIds));
        if (roleButtons.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> buttonIds = roleButtons.stream().map(RoleButton::getButtonId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (buttonIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<MenuButton> menuButtons = menuButtonMapper.selectList(new LambdaQueryWrapper<MenuButton>()
                .in(MenuButton::getId, buttonIds)
                .eq(MenuButton::getStatus, 1));

        return menuButtons.stream()
                .map(MenuButton::getButtonCode)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public IPage<MenuDTO> getMenuList(Integer current, Integer size) {
        long pageNo = current == null || current < 1 ? 1 : current;
        long pageSize = size == null || size < 1 ? 10 : size;

        Page<Menu> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getParentId)
                .orderByAsc(Menu::getOrderNum)
                .orderByAsc(Menu::getId);
        IPage<Menu> menuPage = menuMapper.selectPage(page, wrapper);

        List<Long> menuIds = menuPage.getRecords().stream()
                .map(Menu::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, List<MenuButton>> buttonMap = new LinkedHashMap<>();
        if (!menuIds.isEmpty()) {
            List<MenuButton> buttons = menuButtonMapper.selectList(new LambdaQueryWrapper<MenuButton>()
                    .in(MenuButton::getMenuId, menuIds)
                    .orderByAsc(MenuButton::getId));
            buttonMap = buttons.stream()
                    .collect(Collectors.groupingBy(MenuButton::getMenuId, LinkedHashMap::new, Collectors.toList()));
        }
        final Map<Long, List<MenuButton>> finalButtonMap = buttonMap;

        List<MenuDTO> dtoRecords = menuPage.getRecords().stream()
                .map(item -> toMenuDTO(item, finalButtonMap.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        Page<MenuDTO> dtoPage = new Page<>(menuPage.getCurrent(), menuPage.getSize(), menuPage.getTotal());
        dtoPage.setRecords(dtoRecords);
        return dtoPage;
    }

    @Override
    public List<MenuDTO> getMenuTreeList() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getParentId)
                .orderByAsc(Menu::getOrderNum)
                .orderByAsc(Menu::getId));
        if (menus.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> menuIds = menus.stream().map(Menu::getId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, List<MenuButton>> buttonMap = new LinkedHashMap<>();
        if (!menuIds.isEmpty()) {
            List<MenuButton> buttons = menuButtonMapper.selectList(new LambdaQueryWrapper<MenuButton>()
                    .in(MenuButton::getMenuId, menuIds)
                    .orderByAsc(MenuButton::getId));
            buttonMap = buttons.stream()
                    .collect(Collectors.groupingBy(MenuButton::getMenuId, LinkedHashMap::new, Collectors.toList()));
        }

        Map<Long, MenuDTO> nodeMap = new LinkedHashMap<>();
        for (Menu menu : menus) {
            MenuDTO dto = toMenuDTO(menu, buttonMap.getOrDefault(menu.getId(), Collections.emptyList()));
            nodeMap.put(menu.getId(), dto);
        }

        List<MenuDTO> roots = new ArrayList<>();
        for (Menu menu : menus) {
            MenuDTO node = nodeMap.get(menu.getId());
            Long parentId = defaultLong(menu.getParentId(), 0L);
            if (parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
            } else {
                nodeMap.get(parentId).getChildren().add(node);
            }
        }
        sortMenuTree(roots);
        return roots;
    }

    @Override
    public List<String> getAllPages() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuType, 2)
                .isNotNull(Menu::getRouteName)
                .ne(Menu::getRouteName, "")
                .orderByAsc(Menu::getRouteName));

        Set<String> pages = new LinkedHashSet<>();
        for (Menu menu : menus) {
            if (StringUtils.hasText(menu.getRouteName())) {
                pages.add(menu.getRouteName());
            }
        }
        return new ArrayList<>(pages);
    }

    @Override
    public List<MenuTreeDTO> getMenuTree() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getParentId)
                .orderByAsc(Menu::getOrderNum)
                .orderByAsc(Menu::getId));

        Map<Long, MenuTreeDTO> nodeMap = new LinkedHashMap<>();
        for (Menu menu : menus) {
            MenuTreeDTO node = new MenuTreeDTO();
            node.setId(menu.getId());
            node.setLabel(menu.getMenuName());
            node.setPId(defaultLong(menu.getParentId(), 0L));
            nodeMap.put(menu.getId(), node);
        }

        List<MenuTreeDTO> roots = new ArrayList<>();
        for (Menu menu : menus) {
            MenuTreeDTO node = nodeMap.get(menu.getId());
            Long parentId = defaultLong(menu.getParentId(), 0L);
            if (parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
            } else {
                nodeMap.get(parentId).getChildren().add(node);
            }
        }
        sortTree(roots);
        return roots;
    }

    @Override
    @Transactional
    public void addMenu(MenuOperateRequest request) {
        Menu menu = new Menu();
        fillMenuEntity(menu, request);
        menu.setCreateTime(LocalDateTime.now());
        menuMapper.insert(menu);
        saveMenuButtons(menu.getId(), request.getButtons());
    }

    @Override
    @Transactional
    public void updateMenu(MenuOperateRequest request) {
        if (request.getId() == null) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Menu id is required");
        }
        Menu exist = menuMapper.selectById(request.getId());
        if (exist == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "Menu does not exist");
        }

        fillMenuEntity(exist, request);
        exist.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(exist);

        menuButtonMapper.delete(new LambdaQueryWrapper<MenuButton>().eq(MenuButton::getMenuId, request.getId()));
        saveMenuButtons(request.getId(), request.getButtons());
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        if (id == null) {
            return;
        }
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "Menu does not exist");
        }
        deleteMenuRecursively(id);
    }

    @Override
    @Transactional
    public void batchDeleteMenu(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            deleteMenu(id);
        }
    }

    @Override
    public CurrentUserProfileDTO getCurrentUserProfile(Long userId) {
        if (userId == null) {
            throw new BusinessException(AppErrorCode.USER_NOT_LOGGED_IN);
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User does not exist");
        }

        CurrentUserProfileDTO profile = new CurrentUserProfileDTO();
        profile.setId(user.getId());
        profile.setUserName(defaultString(user.getUserName()));
        profile.setNickName(defaultString(user.getNickName()));
        profile.setUserGender(user.getGender() == null ? "1" : String.valueOf(user.getGender()));
        profile.setUserPhone(defaultString(user.getPhone()));
        profile.setUserEmail(defaultString(user.getEmail()));
        profile.setStatus(user.getStatus() == null ? "1" : String.valueOf(user.getStatus()));
        return profile;
    }

    @Override
    @Transactional
    public void updateCurrentUserProfile(Long userId, UpdateCurrentUserProfileRequest request) {
        if (userId == null) {
            throw new BusinessException(AppErrorCode.USER_NOT_LOGGED_IN);
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User does not exist");
        }

        user.setNickName(defaultString(request.getNickName()));
        user.setGender(request.getUserGender());
        user.setPhone(defaultString(request.getUserPhone()));
        user.setEmail(defaultString(request.getUserEmail()));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    @Transactional
    public void updateCurrentUserPassword(Long userId, UpdateCurrentUserPasswordRequest request) {
        if (userId == null) {
            throw new BusinessException(AppErrorCode.USER_NOT_LOGGED_IN);
        }
        if (!StringUtils.hasText(request.getOldPassword()) || !StringUtils.hasText(request.getNewPassword())) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Old password and new password are required");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(AppErrorCode.RESOURCE_NOT_FOUND, "User does not exist");
        }
        if (!PasswordUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "Old password is incorrect");
        }
        if (Objects.equals(request.getOldPassword(), request.getNewPassword())) {
            throw new BusinessException(AppErrorCode.BAD_REQUEST, "New password cannot be the same as old password");
        }

        user.setPassword(PasswordUtil.encode(request.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    private void deleteMenuRecursively(Long id) {
        List<Menu> children = menuMapper.selectList(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        for (Menu child : children) {
            deleteMenuRecursively(child.getId());
        }
        menuButtonMapper.delete(new LambdaQueryWrapper<MenuButton>().eq(MenuButton::getMenuId, id));
        menuMapper.deleteById(id);
    }

    private void saveUserRoles(Long userId, List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return;
        }

        List<String> distinctCodes = roleCodes.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
        if (distinctCodes.isEmpty()) {
            return;
        }

        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .in(Role::getRoleCode, distinctCodes));
        Map<String, Long> roleCodeMap = roles.stream()
                .collect(Collectors.toMap(Role::getRoleCode, Role::getId, (a, b) -> a));

        for (String roleCode : roleCodes) {
            Long roleId = roleCodeMap.get(roleCode);
            if (roleId == null) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    private void saveMenuButtons(Long menuId, List<MenuButtonDTO> buttons) {
        if (menuId == null || buttons == null || buttons.isEmpty()) {
            return;
        }
        for (MenuButtonDTO button : buttons) {
            if (button == null || !StringUtils.hasText(button.getCode())) {
                continue;
            }
            MenuButton entity = new MenuButton();
            entity.setMenuId(menuId);
            entity.setButtonCode(button.getCode());
            entity.setButtonName(StringUtils.hasText(button.getDesc()) ? button.getDesc() : button.getCode());
            entity.setStatus(1);
            menuButtonMapper.insert(entity);
        }
    }

    private void fillMenuEntity(Menu menu, MenuOperateRequest request) {
        menu.setParentId(defaultLong(request.getParentId(), 0L));
        menu.setMenuType(parseInteger(request.getMenuType(), 1));
        menu.setMenuName(defaultString(request.getMenuName()));
        menu.setRouteName(defaultString(request.getRouteName()));
        menu.setRoutePath(defaultString(request.getRoutePath()));
        menu.setComponent(defaultString(request.getComponent()));
        menu.setIcon(defaultString(request.getIcon()));
        menu.setIconType(parseInteger(request.getIconType(), 1));
        menu.setOrderNum(request.getOrder() == null ? 0 : request.getOrder());
        menu.setStatus(parseInteger(request.getStatus(), 1));
        menu.setHideInMenu(Boolean.TRUE.equals(request.getHideInMenu()) ? 1 : 0);
        menu.setActiveMenu(defaultString(request.getActiveMenu()));
    }

    private MenuDTO toMenuDTO(Menu menu, List<MenuButton> buttons) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setCreateBy(menu.getCreateBy());
        dto.setCreateTime(menu.getCreateTime());
        dto.setUpdateBy(menu.getUpdateBy());
        dto.setUpdateTime(menu.getUpdateTime());
        dto.setStatus(menu.getStatus() == null ? "1" : String.valueOf(menu.getStatus()));

        dto.setParentId(defaultLong(menu.getParentId(), 0L));
        dto.setMenuType(menu.getMenuType() == null ? "1" : String.valueOf(menu.getMenuType()));
        dto.setMenuName(defaultString(menu.getMenuName()));
        dto.setRouteName(defaultString(menu.getRouteName()));
        dto.setRoutePath(defaultString(menu.getRoutePath()));
        dto.setComponent(defaultString(menu.getComponent()));
        dto.setIcon(defaultString(menu.getIcon()));
        dto.setIconType(menu.getIconType() == null ? "1" : String.valueOf(menu.getIconType()));
        dto.setOrder(menu.getOrderNum() == null ? 0 : menu.getOrderNum());

        dto.setI18nKey(StringUtils.hasText(menu.getRouteName()) ? "route." + menu.getRouteName() : null);
        dto.setKeepAlive(false);
        dto.setConstant(false);
        dto.setHref(null);
        dto.setHideInMenu(menu.getHideInMenu() != null && menu.getHideInMenu() == 1);
        dto.setActiveMenu(StringUtils.hasText(menu.getActiveMenu()) ? menu.getActiveMenu() : null);
        dto.setMultiTab(false);
        dto.setFixedIndexInTab(null);
        dto.setQuery(new ArrayList<>());

        List<MenuButtonDTO> buttonDTOs = buttons.stream().map(item -> {
            MenuButtonDTO buttonDTO = new MenuButtonDTO();
            buttonDTO.setCode(item.getButtonCode());
            buttonDTO.setDesc(item.getButtonName());
            return buttonDTO;
        }).collect(Collectors.toList());
        dto.setButtons(buttonDTOs);

        return dto;
    }

    private Integer parseInteger(String val, Integer defaultVal) {
        if (!StringUtils.hasText(val)) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception ignored) {
            return defaultVal;
        }
    }

    private String defaultString(String val) {
        return val == null ? "" : val;
    }

    private Long defaultLong(Long val, Long defaultVal) {
        return val == null ? defaultVal : val;
    }

    private void sortTree(List<MenuTreeDTO> nodes) {
        nodes.sort(Comparator.comparing(MenuTreeDTO::getId));
        for (MenuTreeDTO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }

    private void sortMenuTree(List<MenuDTO> nodes) {
        nodes.sort(Comparator.comparingInt(node -> node.getOrder() == null ? 0 : node.getOrder()));
        for (MenuDTO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortMenuTree(node.getChildren());
            }
        }
    }

    private void validateRoleUnique(String roleName, String roleCode, Long currentRoleId) {
        Role byName = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleName, roleName));
        if (byName != null && (currentRoleId == null || !byName.getId().equals(currentRoleId))) {
            throw new BusinessException(AppErrorCode.RESOURCE_CONFLICT, "Role name already exists");
        }

        Role byCode = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, roleCode));
        if (byCode != null && (currentRoleId == null || !byCode.getId().equals(currentRoleId))) {
            throw new BusinessException(AppErrorCode.RESOURCE_CONFLICT, "Role code already exists");
        }
    }

    private boolean isProtectedRole(Role role) {
        String roleCode = role.getRoleCode() == null ? "" : role.getRoleCode().trim().toUpperCase();
        if ("R_SUPER".equals(roleCode) || "R_ADMIN".equals(roleCode) || "R_USER".equals(roleCode)) {
            return true;
        }

        String roleName = role.getRoleName() == null ? "" : role.getRoleName().trim().toUpperCase();
        return "SUPER_ADMIN".equals(roleName) || "ADMIN".equals(roleName) || "USER".equals(roleName);
    }
}
