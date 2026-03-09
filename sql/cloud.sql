/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : cloud

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 09/03/2026 22:39:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
  `menu_type` tinyint NOT NULL COMMENT '菜单类型：1目录, 2菜单',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由名称',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_type` tinyint NULL DEFAULT 1 COMMENT '图标类型：1=iconify, 2=local',
  `order_num` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '启用状态：1启用, 2禁用',
  `hide_in_menu` tinyint NULL DEFAULT 0 COMMENT '是否隐藏菜单：0否, 1是',
  `active_menu` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '隐藏时高亮菜单路由名称',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2, 0, 1, 'exception', 'exception', '/exception', 'layout.base', 'ant-design:exception-outlined', 1, 7, 1, 0, '', NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 16:26:46');
INSERT INTO `sys_menu` VALUES (3, 2, 2, 'exception_403', 'exception_403', '/exception/403', 'view.403', 'ic:baseline-block', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (4, 2, 2, 'exception_404', 'exception_404', '/exception/404', 'view.404', 'ic:baseline-web-asset-off', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (5, 2, 2, 'exception_500', 'exception_500', '/exception/500', 'view.500', 'ic:baseline-wifi-off', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (6, 0, 1, 'document', 'document', '/document', 'layout.base', 'mdi:file-document-multiple-outline', 1, 2, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (7, 6, 2, 'document_antd', 'document_antd', '/document/antd', 'view.iframe-page', 'logos:ant-design', 1, 7, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (8, 6, 2, 'document_naive', 'document_naive', '/document/naive', 'view.iframe-page', 'logos:naiveui', 1, 6, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (9, 6, 2, 'document_pro-naive', 'document_pro-naive', '/document/pro-naive', 'view.iframe-page', 'logos:naiveui', 1, 6, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (10, 6, 2, 'document_alova', 'document_alova', '/document/alova', 'view.iframe-page', 'alova', 2, 7, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (11, 6, 2, 'document_project', 'document_project', '/document/project', 'view.iframe-page', 'logo', 2, 1, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (12, 6, 2, 'document_project-link', 'document_project-link', '/document/project-link', 'view.iframe-page', 'logo', 2, 2, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (13, 6, 2, 'document_video', 'document_video', '/document/video', 'view.iframe-page', 'logo', 2, 2, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (14, 6, 2, 'document_unocss', 'document_unocss', '/document/unocss', 'view.iframe-page', 'logos:unocss', 1, 5, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (15, 6, 2, 'document_vite', 'document_vite', '/document/vite', 'view.iframe-page', 'logos:vitejs', 1, 4, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (16, 6, 2, 'document_vue', 'document_vue', '/document/vue', 'view.iframe-page', 'logos:vue', 1, 3, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (17, 0, 2, 'about', 'about', '/about', 'layout.base$view.about', 'fluent:book-information-24-regular', 1, 10, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (18, 0, 1, 'alova', 'alova', '/alova', 'layout.base', 'carbon:http', 1, 7, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (19, 18, 2, 'alova_request', 'alova_request', '/alova/request', 'view.alova_request', '', 1, 1, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (20, 18, 2, 'alova_scenes', 'alova_scenes', '/alova/scenes', 'view.alova_scenes', 'cbi:scene-dynamic', 1, 3, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (21, 0, 1, 'function', 'function', '/function', 'layout.base', 'icon-park-outline:all-application', 1, 6, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (22, 21, 1, 'function_hide-child', 'function_hide-child', '/function/hide-child', '', 'material-symbols:filter-list-off', 1, 2, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (23, 22, 2, 'function_hide-child_one', 'function_hide-child_one', '/function/hide-child/one', 'view.function_hide-child_one', 'material-symbols:filter-list-off', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (24, 22, 2, 'function_hide-child_three', 'function_hide-child_three', '/function/hide-child/three', 'view.function_hide-child_three', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (25, 22, 2, 'function_hide-child_two', 'function_hide-child_two', '/function/hide-child/two', 'view.function_hide-child_two', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (26, 21, 2, 'function_multi-tab', 'function_multi-tab', '/function/multi-tab', 'view.function_multi-tab', 'ic:round-tab', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (27, 21, 2, 'function_request', 'function_request', '/function/request', 'view.function_request', 'carbon:network-overlay', 1, 3, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (28, 21, 2, 'function_super-page', 'function_super-page', '/function/super-page', 'view.function_super-page', 'ic:round-supervisor-account', 1, 5, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (29, 21, 2, 'function_tab', 'function_tab', '/function/tab', 'view.function_tab', 'ic:round-tab', 1, 1, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (30, 21, 2, 'function_toggle-auth', 'function_toggle-auth', '/function/toggle-auth', 'view.function_toggle-auth', 'ic:round-construction', 1, 4, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (31, 0, 2, 'home', 'home', '/home', 'layout.base$view.home', 'mdi:monitor-dashboard', 1, 1, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (32, 0, 1, 'manage', 'manage', '/manage', 'layout.base', 'carbon:cloud-service-management', 1, 9, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (33, 32, 2, 'manage_menu', 'manage_menu', '/manage/menu', 'view.manage_menu', 'material-symbols:route', 1, 3, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (34, 32, 2, 'manage_role', 'manage_role', '/manage/role', 'view.manage_role', 'carbon:user-role', 1, 2, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (35, 32, 2, 'manage_user', 'manage_user', '/manage/user', 'view.manage_user', 'ic:round-manage-accounts', 1, 1, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (37, 0, 1, 'multi-menu', 'multi-menu', '/multi-menu', 'layout.base', '', 1, 8, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (38, 37, 1, 'multi-menu_first', 'multi-menu_first', '/multi-menu/first', '', '', 1, 1, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (39, 38, 2, 'multi-menu_first_child', 'multi-menu_first_child', '/multi-menu/first/child', 'view.multi-menu_first_child', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (40, 37, 1, 'multi-menu_second', 'multi-menu_second', '/multi-menu/second', '', '', 1, 2, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (41, 40, 1, 'multi-menu_second_child', 'multi-menu_second_child', '/multi-menu/second/child', '', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (42, 41, 2, 'multi-menu_second_child_home', 'multi-menu_second_child_home', '/multi-menu/second/child/home', 'view.multi-menu_second_child_home', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (43, 0, 1, '插件示例', 'plugin', '/plugin', 'layout.base', 'clarity:plugin-line', 1, 7, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (44, 43, 2, 'plugin_barcode', 'plugin_barcode', '/plugin/barcode', 'view.plugin_barcode', 'ic:round-barcode', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (45, 43, 1, 'plugin_charts', 'plugin_charts', '/plugin/charts', '', 'mdi:chart-areaspline', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (46, 45, 2, 'plugin_charts_antv', 'plugin_charts_antv', '/plugin/charts/antv', 'view.plugin_charts_antv', 'hugeicons:flow-square', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (47, 45, 2, 'plugin_charts_echarts', 'plugin_charts_echarts', '/plugin/charts/echarts', 'view.plugin_charts_echarts', 'simple-icons:apacheecharts', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (48, 45, 2, 'plugin_charts_vchart', 'plugin_charts_vchart', '/plugin/charts/vchart', 'view.plugin_charts_vchart', 'visactor', 2, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (49, 43, 2, 'plugin_copy', 'plugin_copy', '/plugin/copy', 'view.plugin_copy', 'mdi:clipboard-outline', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (50, 43, 1, 'plugin_editor', 'plugin_editor', '/plugin/editor', '', 'icon-park-outline:editor', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (51, 50, 2, 'plugin_editor_markdown', 'plugin_editor_markdown', '/plugin/editor/markdown', 'view.plugin_editor_markdown', 'ri:markdown-line', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (52, 50, 2, 'plugin_editor_quill', 'plugin_editor_quill', '/plugin/editor/quill', 'view.plugin_editor_quill', 'mdi:file-document-edit-outline', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (53, 43, 2, 'plugin_excel', 'plugin_excel', '/plugin/excel', 'view.plugin_excel', 'ri:file-excel-2-line', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (54, 43, 1, 'plugin_gantt', 'plugin_gantt', '/plugin/gantt', '', 'ant-design:bar-chart-outlined', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (55, 54, 2, 'plugin_gantt_dhtmlx', 'plugin_gantt_dhtmlx', '/plugin/gantt/dhtmlx', 'view.plugin_gantt_dhtmlx', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (56, 54, 2, 'plugin_gantt_vtable', 'plugin_gantt_vtable', '/plugin/gantt/vtable', 'view.plugin_gantt_vtable', 'visactor', 2, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (57, 43, 2, 'plugin_icon', 'plugin_icon', '/plugin/icon', 'view.plugin_icon', 'custom-icon', 2, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (58, 43, 2, 'plugin_map', 'plugin_map', '/plugin/map', 'view.plugin_map', 'mdi:map', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (59, 43, 2, 'plugin_pdf', 'plugin_pdf', '/plugin/pdf', 'view.plugin_pdf', 'uiw:file-pdf', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (60, 43, 2, 'plugin_pinyin', 'plugin_pinyin', '/plugin/pinyin', 'view.plugin_pinyin', 'entypo-social:google-hangouts', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (61, 43, 2, 'plugin_print', 'plugin_print', '/plugin/print', 'view.plugin_print', 'mdi:printer', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (62, 43, 2, 'plugin_swiper', 'plugin_swiper', '/plugin/swiper', 'view.plugin_swiper', 'simple-icons:swiper', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (63, 43, 1, 'plugin_tables', 'plugin_tables', '/plugin/tables', '', 'icon-park-outline:table', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (64, 63, 2, 'plugin_tables_vtable', 'plugin_tables_vtable', '/plugin/tables/vtable', 'view.plugin_tables_vtable', 'visactor', 2, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (65, 43, 2, 'plugin_typeit', 'plugin_typeit', '/plugin/typeit', 'view.plugin_typeit', 'mdi:typewriter', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (66, 43, 2, 'plugin_video', 'plugin_video', '/plugin/video', 'view.plugin_video', 'mdi:video', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (67, 0, 1, 'pro-naive', 'pro-naive', '/pro-naive', 'layout.base', 'material-symbols-light:demography-outline-rounded', 1, 7, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (68, 67, 1, 'pro-naive_form', 'pro-naive_form', '/pro-naive/form', '', 'fluent:form-28-regular', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (69, 68, 2, 'pro-naive_form_basic', 'pro-naive_form_basic', '/pro-naive/form/basic', 'view.pro-naive_form_basic', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (70, 68, 2, 'pro-naive_form_query', 'pro-naive_form_query', '/pro-naive/form/query', 'view.pro-naive_form_query', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (71, 68, 2, 'pro-naive_form_step', 'pro-naive_form_step', '/pro-naive/form/step', 'view.pro-naive_form_step', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (72, 67, 1, 'pro-naive_table', 'pro-naive_table', '/pro-naive/table', '', 'mynaui:table', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (73, 72, 2, 'pro-naive_table_remote', 'pro-naive_table_remote', '/pro-naive/table/remote', 'view.pro-naive_table_remote', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');
INSERT INTO `sys_menu` VALUES (74, 72, 2, 'pro-naive_table_row-edit', 'pro-naive_table_row-edit', '/pro-naive/table/row-edit', 'view.pro-naive_table_row-edit', '', 1, 0, 1, 0, NULL, NULL, '2026-03-08 14:54:46', NULL, '2026-03-08 14:54:46');

-- ----------------------------
-- Table structure for sys_menu_button
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_button`;
CREATE TABLE `sys_menu_button`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_id` bigint NOT NULL COMMENT '所属菜单ID',
  `button_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '按钮名称',
  `button_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '按钮编码',
  `status` tinyint NULL DEFAULT 1 COMMENT '启用状态：1启用, 2禁用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sysmenubutton_menu`(`menu_id` ASC) USING BTREE,
  CONSTRAINT `fk_sysmenubutton_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单按钮表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu_button
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `role_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '启用状态：1启用, 2禁用',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'R_SUPER', NULL, 1, NULL, '2026-02-22 21:17:01', NULL, '2026-02-22 21:17:01');
INSERT INTO `sys_role` VALUES (2, '管理员', 'R_ADMIN', NULL, 1, NULL, '2026-02-22 21:17:52', NULL, '2026-02-22 21:17:52');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'R_USER', NULL, 1, NULL, '2026-02-22 21:17:52', NULL, '2026-02-22 21:17:52');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE,
  INDEX `fk_sysrolemenu_menu`(`menu_id` ASC) USING BTREE,
  CONSTRAINT `fk_sysrolemenu_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sysrolemenu_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 28);
INSERT INTO `sys_role_menu` VALUES (4, 1, 34);
INSERT INTO `sys_role_menu` VALUES (2, 2, 32);
INSERT INTO `sys_role_menu` VALUES (3, 2, 33);
INSERT INTO `sys_role_menu` VALUES (5, 2, 35);

-- ----------------------------
-- Table structure for sys_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_route`;
CREATE TABLE `sys_route`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父路由ID，0表示顶级路由',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `menu_type` tinyint NULL DEFAULT 2 COMMENT '路由类型：1目录, 2页面',
  `order_num` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '动态路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_route
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名',
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别：1男, 2女',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '启用状态：1启用, 2禁用',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码（加密存储）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_name`(`user_name` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '管理员', 1, '13893347437', '472096308@qq.com', 1, '{sha256}6CqdCqkzHrSYhah0s5Pf/qycwaYYCuDI+l2rzX+YWXE=', NULL, '2026-02-22 21:16:08', NULL, '2026-03-08 16:57:16');
INSERT INTO `sys_user` VALUES (2, 'xiaobai', '小白', 1, '13893357537', '13893357537@qq.com', 1, '123456', NULL, '2026-03-07 14:07:10', NULL, '2026-03-07 14:07:10');
INSERT INTO `sys_user` VALUES (3, 'test', '小测', 2, '', '', 1, '123456', NULL, '2026-03-07 14:07:37', NULL, '2026-03-07 14:07:37');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `fk_sysuserrole_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_sysuserrole_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sysuserrole_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 3);
INSERT INTO `sys_user_role` VALUES (3, 3, 2);

SET FOREIGN_KEY_CHECKS = 1;
