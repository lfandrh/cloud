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

 Date: 15/03/2026 13:28:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for fortune_client_profile
-- ----------------------------
DROP TABLE IF EXISTS `fortune_client_profile`;
CREATE TABLE `fortune_client_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案主键',
  `user_id` bigint NOT NULL COMMENT '关联系统用户ID（sys_user.id）',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `birth_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出生时刻或时辰，如09:30/巳时',
  `calendar_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SOLAR' COMMENT '历法类型：SOLAR/LUNAR',
  `birth_place` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出生地（省市区）',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1男 2女',
  `privacy_level` tinyint NOT NULL DEFAULT 2 COMMENT '隐私级别：1公开 2仅咨询师可见 3仅本人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '补充说明',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_fcp_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询人档案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_client_profile
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_complaint
-- ----------------------------
DROP TABLE IF EXISTS `fortune_complaint`;
CREATE TABLE `fortune_complaint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '投诉主键',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `complainant_user_id` bigint NOT NULL COMMENT '投诉发起人用户ID',
  `target_user_id` bigint NOT NULL COMMENT '被投诉用户ID',
  `complaint_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉类型：ATTITUDE/TIME/FRAUD/OTHER',
  `description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉描述',
  `evidence_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '证据URL列表（JSON）',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN/PROCESSING/CLOSED',
  `handler_user_id` bigint NULL DEFAULT NULL COMMENT '处理人用户ID（平台）',
  `result` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理结果',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `closed_time` datetime NULL DEFAULT NULL COMMENT '关闭时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC, `created_time` ASC) USING BTREE,
  INDEX `idx_complainant_user_id`(`complainant_user_id` ASC) USING BTREE,
  INDEX `idx_target_user_id`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_handler_user_id`(`handler_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_fc_complainant_user_id` FOREIGN KEY (`complainant_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fc_handler_user_id` FOREIGN KEY (`handler_user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_fc_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fc_target_user_id` FOREIGN KEY (`target_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投诉单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_complaint
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_consult_message
-- ----------------------------
DROP TABLE IF EXISTS `fortune_consult_message`;
CREATE TABLE `fortune_consult_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  `order_id` bigint NOT NULL COMMENT '所属订单ID',
  `sender_user_id` bigint NOT NULL COMMENT '发送人用户ID',
  `msg_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息类型：TEXT/IMAGE/FILE/VOICE',
  `msg_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文本内容',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件地址',
  `sent_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_recalled` tinyint NOT NULL DEFAULT 0 COMMENT '是否撤回：0否 1是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_time`(`order_id` ASC, `sent_time` ASC) USING BTREE,
  INDEX `idx_sender_user_id`(`sender_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_fcm_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_fcm_sender_user_id` FOREIGN KEY (`sender_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_consult_message
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_consult_order
-- ----------------------------
DROP TABLE IF EXISTS `fortune_consult_order`;
CREATE TABLE `fortune_consult_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单主键',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务订单号',
  `client_user_id` bigint NOT NULL COMMENT '咨询人用户ID（sys_user.id）',
  `consultant_user_id` bigint NOT NULL COMMENT '咨询师用户ID（sys_user.id）',
  `service_item_id` bigint NOT NULL COMMENT '服务项ID',
  `slot_id` bigint NULL DEFAULT NULL COMMENT '预约时段ID',
  `order_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态',
  `pay_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID/PAID/REFUNDED',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `paid_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `confirm_deadline` datetime NULL DEFAULT NULL COMMENT '接单截止时间',
  `service_start_time` datetime NULL DEFAULT NULL COMMENT '服务开始时间',
  `service_end_time` datetime NULL DEFAULT NULL COMMENT '服务结束时间',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消/关闭原因',
  `client_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '下单时咨询人信息快照（JSON）',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_client`(`client_user_id` ASC, `created_time` ASC) USING BTREE,
  INDEX `idx_consultant`(`consultant_user_id` ASC, `created_time` ASC) USING BTREE,
  INDEX `idx_status`(`order_status` ASC, `pay_status` ASC) USING BTREE,
  INDEX `idx_service_item_id`(`service_item_id` ASC) USING BTREE,
  INDEX `idx_slot_id`(`slot_id` ASC) USING BTREE,
  CONSTRAINT `fk_fco_client_user_id` FOREIGN KEY (`client_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fco_consultant_user_id` FOREIGN KEY (`consultant_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fco_service_item_id` FOREIGN KEY (`service_item_id`) REFERENCES `fortune_service_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fco_slot_id` FOREIGN KEY (`slot_id`) REFERENCES `fortune_schedule_slot` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_consult_order
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_consult_report
-- ----------------------------
DROP TABLE IF EXISTS `fortune_consult_report`;
CREATE TABLE `fortune_consult_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告主键',
  `order_id` bigint NOT NULL COMMENT '订单ID（一单一主报告）',
  `consultant_user_id` bigint NOT NULL COMMENT '提交咨询师用户ID',
  `report_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告标题',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报告正文',
  `report_file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告附件地址',
  `submitted_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `version` int NOT NULL DEFAULT 1 COMMENT '报告版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_consultant_user_id`(`consultant_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_fcr_consultant_user_id` FOREIGN KEY (`consultant_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fcr_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询报告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_consult_report
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_consultant_cert
-- ----------------------------
DROP TABLE IF EXISTS `fortune_consultant_cert`;
CREATE TABLE `fortune_consultant_cert`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资质记录主键',
  `consultant_id` bigint NOT NULL COMMENT '关联咨询师档案ID',
  `cert_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证件类型：ID_CARD/QUALIFICATION/OTHER',
  `cert_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证件编号（建议脱敏存储）',
  `cert_file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证件文件地址',
  `audit_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注/驳回原因',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_consultant`(`consultant_id` ASC) USING BTREE,
  INDEX `idx_cert_audit_status`(`audit_status` ASC) USING BTREE,
  CONSTRAINT `fk_fcc_consultant_id` FOREIGN KEY (`consultant_id`) REFERENCES `fortune_consultant_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询师资质' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_consultant_cert
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_consultant_profile
-- ----------------------------
DROP TABLE IF EXISTS `fortune_consultant_profile`;
CREATE TABLE `fortune_consultant_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '咨询师档案主键',
  `user_id` bigint NOT NULL COMMENT '关联系统用户ID（sys_user.id）',
  `display_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '前台展示名称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `bio` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '咨询师简介',
  `specialties` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '擅长领域，逗号分隔或JSON',
  `experience_years` int NOT NULL DEFAULT 0 COMMENT '从业年限',
  `price_min` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '服务最低价',
  `price_max` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '服务最高价',
  `audit_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
  `rating` decimal(3, 2) NOT NULL DEFAULT 5.00 COMMENT '平均评分（聚合）',
  `service_count` int NOT NULL DEFAULT 0 COMMENT '累计服务次数（聚合）',
  `is_online` tinyint NOT NULL DEFAULT 0 COMMENT '在线状态：0离线 1在线',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_fortune_consultant_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_online`(`is_online` ASC) USING BTREE,
  CONSTRAINT `fk_fcop_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询师档案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_consultant_profile
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_order_review
-- ----------------------------
DROP TABLE IF EXISTS `fortune_order_review`;
CREATE TABLE `fortune_order_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价主键',
  `order_id` bigint NOT NULL COMMENT '订单ID（一单一评）',
  `client_user_id` bigint NOT NULL COMMENT '评价人用户ID',
  `consultant_user_id` bigint NOT NULL COMMENT '被评价咨询师用户ID',
  `score` tinyint NOT NULL COMMENT '评分（1-5）',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `is_anonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名：0否 1是',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_review_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_consultant_score`(`consultant_user_id` ASC, `score` ASC, `created_time` ASC) USING BTREE,
  INDEX `idx_client_user_id`(`client_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_for_client_user_id` FOREIGN KEY (`client_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_for_consultant_user_id` FOREIGN KEY (`consultant_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_for_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单评价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_order_review
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_payment_record
-- ----------------------------
DROP TABLE IF EXISTS `fortune_payment_record`;
CREATE TABLE `fortune_payment_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录主键',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `pay_channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付渠道：WECHAT/ALIPAY/MOCK',
  `channel_trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '渠道交易号',
  `pay_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'INIT' COMMENT '支付状态：INIT/SUCCESS/FAILED/CLOSED',
  `notify_raw` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '支付回调原文',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_channel_trade_no`(`channel_trade_no` ASC) USING BTREE,
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE,
  CONSTRAINT `fk_fpr_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_payment_record
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_refund_record
-- ----------------------------
DROP TABLE IF EXISTS `fortune_refund_record`;
CREATE TABLE `fortune_refund_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款记录主键',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `refund_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退款单号',
  `refund_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退款状态',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人用户ID（平台）',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_refund_no`(`refund_no` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_refund_status`(`refund_status` ASC) USING BTREE,
  INDEX `idx_audit_user_id`(`audit_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_frr_audit_user_id` FOREIGN KEY (`audit_user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_frr_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_refund_record
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_schedule_slot
-- ----------------------------
DROP TABLE IF EXISTS `fortune_schedule_slot`;
CREATE TABLE `fortune_schedule_slot`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '时段主键',
  `consultant_id` bigint NOT NULL COMMENT '咨询师档案ID',
  `service_item_id` bigint NOT NULL COMMENT '服务项ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `slot_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'AVAILABLE' COMMENT '时段状态：AVAILABLE/LOCKED/BOOKED/EXPIRED/DISABLED',
  `lock_expire_time` datetime NULL DEFAULT NULL COMMENT '锁单过期时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_consultant_time`(`consultant_id` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_slot_status`(`slot_status` ASC) USING BTREE,
  INDEX `fk_fss_service_item_id`(`service_item_id` ASC) USING BTREE,
  CONSTRAINT `fk_fss_consultant_id` FOREIGN KEY (`consultant_id`) REFERENCES `fortune_consultant_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_fss_service_item_id` FOREIGN KEY (`service_item_id`) REFERENCES `fortune_service_item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约时段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_schedule_slot
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_service_item
-- ----------------------------
DROP TABLE IF EXISTS `fortune_service_item`;
CREATE TABLE `fortune_service_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务项主键',
  `consultant_id` bigint NOT NULL COMMENT '服务提供者（咨询师档案ID）',
  `service_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `service_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务形式：TEXT/VOICE/VIDEO',
  `duration_min` int NOT NULL COMMENT '服务时长（分钟）',
  `price_amount` decimal(10, 2) NOT NULL COMMENT '服务定价',
  `intro` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务说明',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ON' COMMENT '状态：ON/OFF',
  `sort_no` int NOT NULL DEFAULT 0 COMMENT '展示排序',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_consultant_status`(`consultant_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `fk_fsi_consultant_id` FOREIGN KEY (`consultant_id`) REFERENCES `fortune_consultant_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '咨询服务项' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_service_item
-- ----------------------------

-- ----------------------------
-- Table structure for fortune_settlement_record
-- ----------------------------
DROP TABLE IF EXISTS `fortune_settlement_record`;
CREATE TABLE `fortune_settlement_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '结算记录主键',
  `order_id` bigint NOT NULL COMMENT '对应订单（一单一结）',
  `consultant_user_id` bigint NOT NULL COMMENT '咨询师用户ID',
  `gross_amount` decimal(10, 2) NOT NULL COMMENT '订单总额',
  `platform_fee` decimal(10, 2) NOT NULL COMMENT '平台抽佣',
  `net_amount` decimal(10, 2) NOT NULL COMMENT '咨询师实得金额',
  `settle_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '结算状态：PENDING/SETTLED/FAILED',
  `settle_time` datetime NULL DEFAULT NULL COMMENT '结算时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_consultant_status`(`consultant_user_id` ASC, `settle_status` ASC) USING BTREE,
  CONSTRAINT `fk_fsr_consultant_user_id` FOREIGN KEY (`consultant_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fsr_order_id` FOREIGN KEY (`order_id`) REFERENCES `fortune_consult_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '结算记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fortune_settlement_record
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 94 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `sys_menu` VALUES (34, 32, 2, 'manage_role', 'manage_role', '/manage/role', 'view.manage_role', 'carbon:user-role', 1, 2, 1, 0, '', NULL, '2026-03-08 14:54:46', NULL, '2026-03-15 11:14:22');
INSERT INTO `sys_menu` VALUES (35, 32, 2, 'manage_user', 'manage_user', '/manage/user', 'view.manage_user', 'ic:round-manage-accounts', 1, 1, 1, 0, '', NULL, '2026-03-08 14:54:46', NULL, '2026-03-15 11:28:59');
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
INSERT INTO `sys_menu` VALUES (76, 0, 1, '命理咨询', 'fortune', '/fortune', 'layout.base', 'mdi:yin-yang', 1, 11, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (77, 76, 1, '咨询人中心', 'fortune_client', '/fortune/client', '', 'mdi:account-heart-outline', 1, 1, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (78, 77, 2, '咨询师大厅', 'fortune_client_consultant_hall', '/fortune/client/consultants', 'view.fortune_client_consultant_hall', 'mdi:store-search-outline', 1, 1, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (79, 77, 2, '我的咨询单', 'fortune_client_order', '/fortune/client/orders', 'view.fortune_client_order', 'mdi:file-document-multiple-outline', 1, 2, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (80, 77, 2, '我的档案', 'fortune_client_profile', '/fortune/client/profile', 'view.fortune_client_profile', 'mdi:card-account-details-outline', 1, 3, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (81, 76, 1, '咨询师中心', 'fortune_consultant', '/fortune/consultant', '', 'mdi:account-tie-outline', 1, 2, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (82, 81, 2, '咨询师工作台', 'fortune_consultant_workbench', '/fortune/consultant/workbench', 'view.fortune_consultant_workbench', 'mdi:view-dashboard-outline', 1, 1, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (83, 81, 2, '服务项管理', 'fortune_consultant_service', '/fortune/consultant/services', 'view.fortune_consultant_service', 'mdi:briefcase-outline', 1, 2, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (84, 81, 2, '排班管理', 'fortune_consultant_schedule', '/fortune/consultant/schedules', 'view.fortune_consultant_schedule', 'mdi:calendar-clock-outline', 1, 3, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (85, 81, 2, '咨询单管理', 'fortune_consultant_order', '/fortune/consultant/orders', 'view.fortune_consultant_order', 'mdi:clipboard-list-outline', 1, 4, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (86, 81, 2, '收益中心', 'fortune_consultant_income', '/fortune/consultant/income', 'view.fortune_consultant_income', 'mdi:cash-multiple', 1, 5, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (87, 76, 1, '运营管理', 'fortune_admin', '/fortune/admin', '', 'mdi:shield-account-outline', 1, 3, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (88, 87, 2, '咨询师审核', 'fortune_admin_consultant_audit', '/fortune/admin/consultants/audit', 'view.fortune_admin_consultant_audit', 'mdi:account-check-outline', 1, 1, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (89, 87, 2, '订单管理', 'fortune_admin_order_manage', '/fortune/admin/orders', 'view.fortune_admin_order_manage', 'mdi:order-bool-ascending-variant', 1, 2, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (90, 87, 2, '投诉管理', 'fortune_admin_complaint', '/fortune/admin/complaints', 'view.fortune_admin_complaint', 'mdi:message-alert-outline', 1, 3, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (91, 87, 2, '运营报表', 'fortune_admin_report', '/fortune/admin/reports', 'view.fortune_admin_report', 'mdi:chart-box-outline', 1, 4, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (92, 87, 2, '退款审核', 'fortune_admin_refund', '/fortune/admin/refunds', 'view.fortune_admin_refund', 'mdi:cash-refund', 1, 5, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_menu` VALUES (93, 87, 2, '结算管理', 'fortune_admin_settlement', '/fortune/admin/settlements', 'view.fortune_admin_settlement', 'mdi:bank-transfer-out', 1, 6, 1, 0, NULL, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');

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
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单按钮表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu_button
-- ----------------------------
INSERT INTO `sys_menu_button` VALUES (1, 34, '新增用户', 'user:add', 1);
INSERT INTO `sys_menu_button` VALUES (3, 35, '用户列表', 'user:list', 1);
INSERT INTO `sys_menu_button` VALUES (4, 35, '新增用户', 'user:add', 1);
INSERT INTO `sys_menu_button` VALUES (5, 79, '下单', 'create', 1);
INSERT INTO `sys_menu_button` VALUES (6, 79, '支付', 'pay', 1);
INSERT INTO `sys_menu_button` VALUES (7, 79, '取消', 'cancel', 1);
INSERT INTO `sys_menu_button` VALUES (8, 79, '评价', 'review', 1);
INSERT INTO `sys_menu_button` VALUES (9, 83, '新增', 'add', 1);
INSERT INTO `sys_menu_button` VALUES (10, 83, '编辑', 'edit', 1);
INSERT INTO `sys_menu_button` VALUES (11, 83, '删除', 'delete', 1);
INSERT INTO `sys_menu_button` VALUES (12, 83, '上架', 'on_shelf', 1);
INSERT INTO `sys_menu_button` VALUES (13, 83, '下架', 'off_shelf', 1);
INSERT INTO `sys_menu_button` VALUES (14, 84, '批量发布', 'publish', 1);
INSERT INTO `sys_menu_button` VALUES (15, 84, '关闭时段', 'close_slot', 1);
INSERT INTO `sys_menu_button` VALUES (16, 85, '接单', 'confirm', 1);
INSERT INTO `sys_menu_button` VALUES (17, 85, '拒单', 'reject', 1);
INSERT INTO `sys_menu_button` VALUES (18, 85, '提交结论', 'submit_report', 1);
INSERT INTO `sys_menu_button` VALUES (19, 88, '通过', 'approve', 1);
INSERT INTO `sys_menu_button` VALUES (20, 88, '驳回', 'reject', 1);
INSERT INTO `sys_menu_button` VALUES (21, 90, '受理', 'accept', 1);
INSERT INTO `sys_menu_button` VALUES (22, 90, '关闭', 'close', 1);
INSERT INTO `sys_menu_button` VALUES (23, 92, '通过', 'approve', 1);
INSERT INTO `sys_menu_button` VALUES (24, 92, '驳回', 'reject', 1);
INSERT INTO `sys_menu_button` VALUES (25, 93, '执行结算', 'settle', 1);
INSERT INTO `sys_menu_button` VALUES (26, 93, '导出', 'export', 1);
INSERT INTO `sys_menu_button` VALUES (27, 91, '查询', 'query', 1);
INSERT INTO `sys_menu_button` VALUES (28, 91, '导出', 'export', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'R_SUPER', NULL, 1, NULL, '2026-02-22 21:17:01', NULL, '2026-02-22 21:17:01');
INSERT INTO `sys_role` VALUES (2, '管理员', 'R_ADMIN', NULL, 1, NULL, '2026-02-22 21:17:52', NULL, '2026-02-22 21:17:52');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'R_USER', NULL, 1, NULL, '2026-02-22 21:17:52', NULL, '2026-03-15 12:23:20');
INSERT INTO `sys_role` VALUES (5, '咨询人', 'fortune_client', '命理咨询系统-咨询人角色', 1, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_role` VALUES (6, '命理咨询师', 'fortune_consultant', '命理咨询系统-咨询师角色', 1, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_role` VALUES (7, '运营管理员', 'fortune_op_admin', '命理咨询系统-运营管理员角色', 1, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');
INSERT INTO `sys_role` VALUES (8, '财务管理员', 'fortune_finance_admin', '命理咨询系统-财务管理员角色', 1, NULL, '2026-03-15 13:26:38', NULL, '2026-03-15 13:26:38');

-- ----------------------------
-- Table structure for sys_role_button
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_button`;
CREATE TABLE `sys_role_button`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT 'role id',
  `button_id` bigint NOT NULL COMMENT 'menu button id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_role_button`(`role_id` ASC, `button_id` ASC) USING BTREE,
  INDEX `fk_sysrolebutton_button`(`button_id` ASC) USING BTREE,
  CONSTRAINT `fk_sysrolebutton_button` FOREIGN KEY (`button_id`) REFERENCES `sys_menu_button` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sysrolebutton_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'role button relation' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_button
-- ----------------------------
INSERT INTO `sys_role_button` VALUES (1, 3, 3);
INSERT INTO `sys_role_button` VALUES (2, 3, 4);

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
) ENGINE = InnoDB AUTO_INCREMENT = 99 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 28);
INSERT INTO `sys_role_menu` VALUES (4, 1, 34);
INSERT INTO `sys_role_menu` VALUES (36, 1, 76);
INSERT INTO `sys_role_menu` VALUES (38, 1, 77);
INSERT INTO `sys_role_menu` VALUES (40, 1, 78);
INSERT INTO `sys_role_menu` VALUES (42, 1, 79);
INSERT INTO `sys_role_menu` VALUES (44, 1, 80);
INSERT INTO `sys_role_menu` VALUES (46, 1, 81);
INSERT INTO `sys_role_menu` VALUES (48, 1, 82);
INSERT INTO `sys_role_menu` VALUES (50, 1, 83);
INSERT INTO `sys_role_menu` VALUES (52, 1, 84);
INSERT INTO `sys_role_menu` VALUES (54, 1, 85);
INSERT INTO `sys_role_menu` VALUES (56, 1, 86);
INSERT INTO `sys_role_menu` VALUES (58, 1, 87);
INSERT INTO `sys_role_menu` VALUES (60, 1, 88);
INSERT INTO `sys_role_menu` VALUES (62, 1, 89);
INSERT INTO `sys_role_menu` VALUES (64, 1, 90);
INSERT INTO `sys_role_menu` VALUES (66, 1, 91);
INSERT INTO `sys_role_menu` VALUES (68, 1, 92);
INSERT INTO `sys_role_menu` VALUES (70, 1, 93);
INSERT INTO `sys_role_menu` VALUES (2, 2, 32);
INSERT INTO `sys_role_menu` VALUES (3, 2, 33);
INSERT INTO `sys_role_menu` VALUES (5, 2, 35);
INSERT INTO `sys_role_menu` VALUES (37, 2, 76);
INSERT INTO `sys_role_menu` VALUES (39, 2, 77);
INSERT INTO `sys_role_menu` VALUES (41, 2, 78);
INSERT INTO `sys_role_menu` VALUES (43, 2, 79);
INSERT INTO `sys_role_menu` VALUES (45, 2, 80);
INSERT INTO `sys_role_menu` VALUES (47, 2, 81);
INSERT INTO `sys_role_menu` VALUES (49, 2, 82);
INSERT INTO `sys_role_menu` VALUES (51, 2, 83);
INSERT INTO `sys_role_menu` VALUES (53, 2, 84);
INSERT INTO `sys_role_menu` VALUES (55, 2, 85);
INSERT INTO `sys_role_menu` VALUES (57, 2, 86);
INSERT INTO `sys_role_menu` VALUES (59, 2, 87);
INSERT INTO `sys_role_menu` VALUES (61, 2, 88);
INSERT INTO `sys_role_menu` VALUES (63, 2, 89);
INSERT INTO `sys_role_menu` VALUES (65, 2, 90);
INSERT INTO `sys_role_menu` VALUES (67, 2, 91);
INSERT INTO `sys_role_menu` VALUES (69, 2, 92);
INSERT INTO `sys_role_menu` VALUES (71, 2, 93);
INSERT INTO `sys_role_menu` VALUES (7, 3, 35);
INSERT INTO `sys_role_menu` VALUES (8, 5, 76);
INSERT INTO `sys_role_menu` VALUES (9, 5, 77);
INSERT INTO `sys_role_menu` VALUES (10, 5, 78);
INSERT INTO `sys_role_menu` VALUES (11, 5, 79);
INSERT INTO `sys_role_menu` VALUES (12, 5, 80);
INSERT INTO `sys_role_menu` VALUES (15, 6, 76);
INSERT INTO `sys_role_menu` VALUES (16, 6, 81);
INSERT INTO `sys_role_menu` VALUES (17, 6, 82);
INSERT INTO `sys_role_menu` VALUES (18, 6, 83);
INSERT INTO `sys_role_menu` VALUES (19, 6, 84);
INSERT INTO `sys_role_menu` VALUES (20, 6, 85);
INSERT INTO `sys_role_menu` VALUES (21, 6, 86);
INSERT INTO `sys_role_menu` VALUES (22, 7, 76);
INSERT INTO `sys_role_menu` VALUES (23, 7, 87);
INSERT INTO `sys_role_menu` VALUES (24, 7, 88);
INSERT INTO `sys_role_menu` VALUES (25, 7, 89);
INSERT INTO `sys_role_menu` VALUES (26, 7, 90);
INSERT INTO `sys_role_menu` VALUES (27, 7, 91);
INSERT INTO `sys_role_menu` VALUES (29, 8, 76);
INSERT INTO `sys_role_menu` VALUES (30, 8, 87);
INSERT INTO `sys_role_menu` VALUES (31, 8, 91);
INSERT INTO `sys_role_menu` VALUES (32, 8, 92);
INSERT INTO `sys_role_menu` VALUES (33, 8, 93);

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
INSERT INTO `sys_user` VALUES (1, 'admin', '管理员', 1, '13893347437', '472096308@qq.com', 1, '{bcrypt}$2a$10$6kfHGoEhounUxh/V4G5AsemMS2aCr5VW2ih.iOUlIdzU3VkfzqLM6', NULL, '2026-02-22 21:16:08', NULL, '2026-03-08 16:57:16');
INSERT INTO `sys_user` VALUES (2, 'xiaobai', '小白', 1, '13893357537', '13893357537@qq.com', 1, '{bcrypt}$2a$10$kSi5BHxKDVkrZzp8YUSB7Osb9vsn6F4AIZr28tJOC0Z0AhN70tU72', NULL, '2026-03-07 14:07:10', NULL, '2026-03-07 14:07:10');
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
