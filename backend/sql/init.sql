/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : counselor_platform

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 26/09/2026 13:00:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment_order
-- ----------------------------
DROP TABLE IF EXISTS `appointment_order`;
CREATE TABLE `appointment_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号(业务唯一)',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `counselor_id` bigint NOT NULL COMMENT '咨询师ID',
  `schedule_id` bigint NOT NULL COMMENT '时段ID',
  `appointment_time` datetime NOT NULL COMMENT '预约时间(冗余，便于查询)',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待确认 1-已确认 2-已完成 3-已取消',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户备注',
  `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `schedule_id_active` bigint GENERATED ALWAYS AS (if((`is_deleted` = 0),`schedule_id`,NULL)) STORED NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_schedule_id_active`(`schedule_id_active` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_counselor_status`(`counselor_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '预约订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment_order
-- ----------------------------
INSERT INTO `appointment_order` VALUES (1, '2103031140960444416', 1, 1, 1, '2026-09-25 09:00:00', 0, '', NULL, '2026-09-24 15:57:51', '2026-09-24 15:57:51', 0, DEFAULT);
INSERT INTO `appointment_order` VALUES (2, '2103031642234298368', 1, 3, 71, '2026-09-25 09:00:00', 0, '', NULL, '2026-09-24 15:59:51', '2026-09-24 15:59:51', 0, DEFAULT);
INSERT INTO `appointment_order` VALUES (3, '2103041904928649216', 1, 1, 2, '2026-09-25 10:00:00', 0, '', NULL, '2026-09-24 16:40:37', '2026-09-24 16:40:37', 0, DEFAULT);
INSERT INTO `appointment_order` VALUES (4, '2103673424307585024', 1, 1, 6, '2026-09-26 09:00:00', 0, '', NULL, '2026-09-26 10:30:03', '2026-09-26 10:30:03', 0, DEFAULT);

-- ----------------------------
-- Table structure for counselor
-- ----------------------------
DROP TABLE IF EXISTS `counselor`;
CREATE TABLE `counselor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '咨询师ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `specialties` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '擅长领域，多个用逗号分隔，如：焦虑,抑郁,青少年',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职称，如：国家二级心理咨询师',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '简介',
  `qualification` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资质证书描述',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '单次咨询价格(元)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架 1-上架',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序权重，越大越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status_sort`(`status` ASC, `sort_order` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '咨询师表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of counselor
-- ----------------------------
INSERT INTO `counselor` VALUES (1, '张老师', NULL, '焦虑,抑郁', '国家二级心理咨询师', '从业 10 年，擅长认知行为疗法', '国家二级证书', 300.00, 1, 10, '2026-09-19 12:52:26', '2026-09-19 12:52:26', 0);
INSERT INTO `counselor` VALUES (2, '李老师', NULL, '青少年,亲子关系', '国家三级心理咨询师', '专注青少年心理辅导', '国家三级证书', 200.00, 1, 5, '2026-09-19 12:52:26', '2026-09-19 12:52:26', 0);
INSERT INTO `counselor` VALUES (3, '王老师', NULL, '婚姻,情感', '婚姻家庭咨询师', '擅长婚姻情感咨询', '婚姻家庭咨询师证书', 400.00, 1, 8, '2026-09-19 12:52:26', '2026-09-19 12:52:26', 0);

-- ----------------------------
-- Table structure for counselor_schedule
-- ----------------------------
DROP TABLE IF EXISTS `counselor_schedule`;
CREATE TABLE `counselor_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '时段ID',
  `counselor_id` bigint NOT NULL COMMENT '咨询师ID',
  `schedule_date` date NOT NULL COMMENT '日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-可约 1-已约 2-不可约',
  `order_id` bigint NULL DEFAULT NULL COMMENT '占用订单ID，status=1时非空',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_counselor_time`(`counselor_id` ASC, `schedule_date` ASC, `start_time` ASC, `end_time` ASC, `is_deleted` ASC) USING BTREE COMMENT '同一咨询师同一时段唯一',
  INDEX `idx_counselor_date`(`counselor_id` ASC, `schedule_date` ASC, `status` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '咨询师可预约时段表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of counselor_schedule
-- ----------------------------
INSERT INTO `counselor_schedule` VALUES (1, 1, '2026-09-25', '09:00:00', '10:00:00', 1, 1, '2026-09-24 15:57:10', '2026-09-24 15:58:51', 0);
INSERT INTO `counselor_schedule` VALUES (2, 1, '2026-09-25', '10:00:00', '11:00:00', 1, 3, '2026-09-24 15:57:10', '2026-09-24 16:41:37', 0);
INSERT INTO `counselor_schedule` VALUES (3, 1, '2026-09-25', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (4, 1, '2026-09-25', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (5, 1, '2026-09-25', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (6, 1, '2026-09-26', '09:00:00', '10:00:00', 1, 4, '2026-09-24 15:57:10', '2026-09-26 10:30:03', 0);
INSERT INTO `counselor_schedule` VALUES (7, 1, '2026-09-26', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (8, 1, '2026-09-26', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (9, 1, '2026-09-26', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (10, 1, '2026-09-26', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (11, 1, '2026-09-27', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (12, 1, '2026-09-27', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (13, 1, '2026-09-27', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (14, 1, '2026-09-27', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (15, 1, '2026-09-27', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (16, 1, '2026-09-28', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (17, 1, '2026-09-28', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (18, 1, '2026-09-28', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (19, 1, '2026-09-28', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (20, 1, '2026-09-28', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (21, 1, '2026-09-29', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (22, 1, '2026-09-29', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (23, 1, '2026-09-29', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (24, 1, '2026-09-29', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:10', '2026-09-24 15:57:10', 0);
INSERT INTO `counselor_schedule` VALUES (25, 1, '2026-09-29', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (26, 1, '2026-09-30', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (27, 1, '2026-09-30', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (28, 1, '2026-09-30', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (29, 1, '2026-09-30', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (30, 1, '2026-09-30', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (31, 1, '2026-10-01', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (32, 1, '2026-10-01', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (33, 1, '2026-10-01', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (34, 1, '2026-10-01', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (35, 1, '2026-10-01', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (36, 2, '2026-09-25', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (37, 2, '2026-09-25', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (38, 2, '2026-09-25', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (39, 2, '2026-09-25', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (40, 2, '2026-09-25', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (41, 2, '2026-09-26', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (42, 2, '2026-09-26', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (43, 2, '2026-09-26', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (44, 2, '2026-09-26', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (45, 2, '2026-09-26', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (46, 2, '2026-09-27', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (47, 2, '2026-09-27', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (48, 2, '2026-09-27', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (49, 2, '2026-09-27', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (50, 2, '2026-09-27', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (51, 2, '2026-09-28', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (52, 2, '2026-09-28', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (53, 2, '2026-09-28', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (54, 2, '2026-09-28', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (55, 2, '2026-09-28', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (56, 2, '2026-09-29', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (57, 2, '2026-09-29', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (58, 2, '2026-09-29', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (59, 2, '2026-09-29', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (60, 2, '2026-09-29', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (61, 2, '2026-09-30', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (62, 2, '2026-09-30', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (63, 2, '2026-09-30', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (64, 2, '2026-09-30', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (65, 2, '2026-09-30', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (66, 2, '2026-10-01', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (67, 2, '2026-10-01', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (68, 2, '2026-10-01', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (69, 2, '2026-10-01', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (70, 2, '2026-10-01', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (71, 3, '2026-09-25', '09:00:00', '10:00:00', 1, 2, '2026-09-24 15:57:11', '2026-09-24 16:00:50', 0);
INSERT INTO `counselor_schedule` VALUES (72, 3, '2026-09-25', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (73, 3, '2026-09-25', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (74, 3, '2026-09-25', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (75, 3, '2026-09-25', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (76, 3, '2026-09-26', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (77, 3, '2026-09-26', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (78, 3, '2026-09-26', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (79, 3, '2026-09-26', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (80, 3, '2026-09-26', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (81, 3, '2026-09-27', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (82, 3, '2026-09-27', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (83, 3, '2026-09-27', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (84, 3, '2026-09-27', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (85, 3, '2026-09-27', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (86, 3, '2026-09-28', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (87, 3, '2026-09-28', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (88, 3, '2026-09-28', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (89, 3, '2026-09-28', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (90, 3, '2026-09-28', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (91, 3, '2026-09-29', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (92, 3, '2026-09-29', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (93, 3, '2026-09-29', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (94, 3, '2026-09-29', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (95, 3, '2026-09-29', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (96, 3, '2026-09-30', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (97, 3, '2026-09-30', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (98, 3, '2026-09-30', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (99, 3, '2026-09-30', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (100, 3, '2026-09-30', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (101, 3, '2026-10-01', '09:00:00', '10:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (102, 3, '2026-10-01', '10:00:00', '11:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (103, 3, '2026-10-01', '14:00:00', '15:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (104, 3, '2026-10-01', '15:00:00', '16:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);
INSERT INTO `counselor_schedule` VALUES (105, 3, '2026-10-01', '16:00:00', '17:00:00', 0, NULL, '2026-09-24 15:57:11', '2026-09-24 15:57:11', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_type` tinyint NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'test', '123456', '测试用户', NULL, 0, 1, '2026-09-20 12:08:53', '2026-09-20 12:08:53', 0);

SET FOREIGN_KEY_CHECKS = 1;
