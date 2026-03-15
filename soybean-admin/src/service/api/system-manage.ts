import { request } from '../request';

/** get role list */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  return request<Api.SystemManage.RoleList>({
    url: '/user/getRoleList',
    method: 'get',
    params
  });
}

/**
 * get all roles
 *
 * these roles are all enabled
 */
export function fetchGetAllRoles() {
  return request<Api.SystemManage.AllRole[]>({
    url: '/user/getAllRoles',
    method: 'get'
  });
}

export type RoleModel = Pick<Api.SystemManage.Role, 'id' | 'roleName' | 'roleCode' | 'roleDesc' | 'status'>;

/** add role */
export function fetchAddRole(data: Omit<RoleModel, 'id'>) {
  return request<null>({
    url: '/user/addRole',
    method: 'post',
    data
  });
}

/** update role */
export function fetchUpdateRole(data: RoleModel) {
  return request<null>({
    url: '/user/updateRole',
    method: 'post',
    data
  });
}

/** delete role */
export function fetchDeleteRole(id: number) {
  return request<null>({
    url: '/user/deleteRole',
    method: 'delete',
    params: { id }
  });
}

/** batch delete role */
export function fetchBatchDeleteRole(ids: number[]) {
  return request<null>({
    url: '/user/batchDeleteRole',
    method: 'delete',
    params: { ids: ids.join(',') }
  });
}

/** get role menu ids */
export function fetchGetRoleMenuIds(roleId: number) {
  return request<number[]>({
    url: '/user/getRoleMenuIds',
    method: 'get',
    params: { roleId }
  });
}

/** update role menus */
export function fetchUpdateRoleMenus(roleId: number, menuIds: number[]) {
  return request<null>({
    url: '/user/updateRoleMenus',
    method: 'post',
    data: {
      id: roleId,
      menuIds
    }
  });
}

/** get button tree */
export function fetchGetButtonTree() {
  return request<Api.SystemManage.ButtonTree[]>({
    url: '/user/getButtonTree',
    method: 'get'
  });
}

/** get role button ids */
export function fetchGetRoleButtonIds(roleId: number) {
  return request<number[]>({
    url: '/user/getRoleButtonIds',
    method: 'get',
    params: { roleId }
  });
}

/** update role buttons */
export function fetchUpdateRoleButtons(roleId: number, buttonIds: number[]) {
  return request<null>({
    url: '/user/updateRoleButtons',
    method: 'post',
    data: {
      id: roleId,
      buttonIds
    }
  });
}

/** get user list */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  return request<Api.SystemManage.UserList>({
    url: '/user/getUserList',
    method: 'get',
    params
  });
}

export type UserModel = Pick<
  Api.SystemManage.User,
  'id' | 'userName' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail' | 'userRoles' | 'status'
>;

/** add user */
export function fetchAddUser(data: UserModel) {
  return request<null>({
    url: '/user/addUser',
    method: 'post',
    data
  });
}

/** update user */
export function fetchUpdateUser(data: UserModel) {
  return request<null>({
    url: '/user/updateUser',
    method: 'post',
    data
  });
}

/** delete user */
export function fetchDeleteUser(id: number) {
  return request<null>({
    url: '/user/deleteUser',
    method: 'delete',
    params: { id }
  });
}

/** batch delete user */
export function fetchBatchDeleteUser(ids: number[]) {
  return request<null>({
    url: '/user/batchDeleteUser',
    method: 'delete',
    params: { ids: ids.join(',') }
  });
}

/** get current user profile */
export function fetchGetCurrentUserProfile() {
  return request<Api.SystemManage.CurrentUserProfile>({
    url: '/user/getCurrentUserProfile',
    method: 'get'
  });
}

/** update current user profile */
export function fetchUpdateCurrentUserProfile(data: Api.SystemManage.UpdateCurrentUserProfileParams) {
  return request<null>({
    url: '/user/updateCurrentUserProfile',
    method: 'post',
    data
  });
}

/** update current user password */
export function fetchUpdateCurrentUserPassword(data: Api.SystemManage.UpdateCurrentUserPasswordParams) {
  return request<null>({
    url: '/user/updateCurrentUserPassword',
    method: 'post',
    data
  });
}

/** get menu list */
export function fetchGetMenuList(params?: Api.SystemManage.CommonSearchParams) {
  return request<Api.SystemManage.MenuList>({
    url: '/user/getMenuList/v2',
    method: 'get',
    params
  });
}

/** get menu tree list */
export function fetchGetMenuTreeList() {
  return request<Api.SystemManage.Menu[]>({
    url: '/user/getMenuTreeList',
    method: 'get'
  });
}

/** get all pages */
export function fetchGetAllPages() {
  return request<string[]>({
    url: '/user/getAllPages',
    method: 'get'
  });
}

/** get menu tree */
export function fetchGetMenuTree() {
  return request<Api.SystemManage.MenuTree[]>({
    url: '/user/getMenuTree',
    method: 'get'
  });
}

export type MenuModel = Omit<Api.SystemManage.Menu, 'id' | 'createBy' | 'createTime' | 'updateBy' | 'updateTime'>;

/** add menu */
export function fetchAddMenu(data: MenuModel) {
  return request<null>({
    url: '/user/addMenu',
    method: 'post',
    data
  });
}

/** update menu */
export function fetchUpdateMenu(data: MenuModel & Pick<Api.SystemManage.Menu, 'id'>) {
  return request<null>({
    url: '/user/updateMenu',
    method: 'post',
    data
  });
}

/** delete menu */
export function fetchDeleteMenu(id: number) {
  return request<null>({
    url: '/user/deleteMenu',
    method: 'delete',
    params: { id }
  });
}

/** batch delete menu */
export function fetchBatchDeleteMenu(ids: number[]) {
  return request<null>({
    url: '/user/batchDeleteMenu',
    method: 'delete',
    params: { ids: ids.join(',') }
  });
}
