import { alova } from '../request';

/** get role list */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  return alova.Get<Api.SystemManage.RoleList>('/user/getRoleList', { params });
}

/**
 * get all roles
 *
 * these roles are all enabled
 */
export function fetchGetAllRoles() {
  return alova.Get<Api.SystemManage.AllRole[]>('/user/getAllRoles');
}

/** get user list */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  return alova.Get<Api.SystemManage.UserList>('/user/getUserList', { params });
}

export type UserModel = Pick<
  Api.SystemManage.User,
  'userName' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail' | 'userRoles' | 'status'
>;
/** add user */
export function addUser(data: UserModel) {
  return alova.Post<null>('/user/addUser', data);
}

/** update user */
export function updateUser(data: UserModel) {
  return alova.Post<null>('/user/updateUser', data);
}

/** delete user */
export function deleteUser(id: number) {
  return alova.Delete<null>('/user/deleteUser', { id });
}

/** batch delete user */
export function batchDeleteUser(ids: number[]) {
  return alova.Delete<null>('/user/batchDeleteUser', { ids });
}

/** get menu list */
export function fetchGetMenuList() {
  return alova.Get<Api.SystemManage.MenuList>('/user/getMenuList/v2');
}

/** get all pages */
export function fetchGetAllPages() {
  return alova.Get<string[]>('/user/getAllPages');
}

/** get menu tree */
export function fetchGetMenuTree() {
  return alova.Get<Api.SystemManage.MenuTree[]>('/user/getMenuTree');
}
