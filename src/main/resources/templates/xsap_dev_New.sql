/*
 Navicat Premium Data Transfer

 Source Server         : RDS_aliyun
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : rm-bp1hul438hq66s09ubo.mysql.rds.aliyuncs.com:3306
 Source Schema         : xsap_dev

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 30/12/2021 15:21:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_class_record
-- ----------------------------
DROP TABLE IF EXISTS `t_class_record`;
CREATE TABLE `t_class_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '会员id',
  `card_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员卡名',
  `schedule_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '排课记录id',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '教师评语',
  `check_status` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '用户确认上课与否。1，已上课；0，未上课',
  `reserve_check` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否已预约，默认0',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `last_modify_time` datetime(0) NULL DEFAULT NULL,
  `version` int(10) UNSIGNED NULL DEFAULT 1,
  `bind_card_id` int(11) NULL DEFAULT NULL COMMENT '绑定会员卡id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_class_member_id`(`member_id`) USING BTREE,
  INDEX `fk_class_schedule_id`(`schedule_id`) USING BTREE,
  CONSTRAINT `fk_class_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_class_schedule_id` FOREIGN KEY (`schedule_id`) REFERENCES `t_schedule_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_class_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_consume_record
-- ----------------------------
DROP TABLE IF EXISTS `t_consume_record`;
CREATE TABLE `t_consume_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `operate_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作类型',
  `card_count_change` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '卡次变化',
  `card_day_change` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '有效天数变化',
  `money_cost` decimal(10, 2) UNSIGNED NULL COMMENT '花费的金额',
  `operator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作员',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `member_bind_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '会员绑定id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  `log_Id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '操作记录id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_consume_member_bind_id`(`member_bind_id`) USING BTREE,
  INDEX `t_consume_record_t_member_log_id_fk`(`log_Id`) USING BTREE,
  CONSTRAINT `fk_consume_member_bind_id` FOREIGN KEY (`member_bind_id`) REFERENCES `t_member_bind_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_consume_record_t_member_log_id_fk` FOREIGN KEY (`log_Id`) REFERENCES `t_member_log` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '消费记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_consume_record
-- ----------------------------
INSERT INTO `t_consume_record` VALUES (39, '绑卡操作', 0, 0, 1.00, '张老师', '办卡的费用', 24, '2021-12-30 13:46:44', NULL, 1, NULL);
INSERT INTO `t_consume_record` VALUES (40, '绑卡操作', 0, 0, 1.00, '张老师', '办卡的费用', 25, '2021-12-30 13:54:54', NULL, 1, NULL);
INSERT INTO `t_consume_record` VALUES (41, '绑卡操作', 0, 0, 1.00, '张老师', '办卡的费用', 26, '2021-12-30 13:56:39', NULL, 1, NULL);
INSERT INTO `t_consume_record` VALUES (42, '绑卡操作', 0, 0, 1.00, '张老师', '办卡的费用', 27, '2021-12-30 14:03:19', NULL, 1, NULL);
INSERT INTO `t_consume_record` VALUES (43, '绑卡操作', 0, 0, 1.00, '张老师', '办卡的费用', 28, '2021-12-30 14:37:08', NULL, 1, NULL);
INSERT INTO `t_consume_record` VALUES (44, '绑卡操作', 0, 0, 999.00, '张老师', '办卡的费用', 29, '2021-12-30 14:39:24', NULL, 1, NULL);

-- ----------------------------
-- Table structure for t_course
-- ----------------------------
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `duration` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '课程时长',
  `contains` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '课堂容纳人数',
  `color` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卡片颜色',
  `introduce` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程介绍',
  `times_cost` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '每节课程需花费的次数',
  `limit_sex` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '限制性别',
  `limit_age` int(10) NULL DEFAULT NULL COMMENT '限制年龄',
  `limit_counts` int(10) NULL DEFAULT NULL COMMENT '限制预约次数',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_course
-- ----------------------------
INSERT INTO `t_course` VALUES (42, 'MySQL', 45, 20, '#c91616', '<p>MySQL基础</p>', 1, '无限制', 5, 2, '2021-12-30 11:37:00', NULL, 2);
INSERT INTO `t_course` VALUES (43, 'Spring框架', 50, 30, '#1974b5', '<p>spring三剑客</p>', 2, '无限制', 5, 2, '2021-12-30 11:37:46', NULL, 2);
INSERT INTO `t_course` VALUES (44, 'Linux', 50, 20, '#18cccc', '<p>Linux基础操作</p>', 3, '无限制', 7, 2, '2021-12-30 11:38:53', NULL, 2);
INSERT INTO `t_course` VALUES (45, 'ElasticSearch', 40, 30, '#201ce6', '<p>ES基础语法</p>', 3, '男', 9, 2, '2021-12-30 11:40:37', NULL, 2);
INSERT INTO `t_course` VALUES (46, 'SpringBoot', 35, 10, '#cf2562', '<p>SpringBoot进阶</p>', 5, '无限制', 5, 2, '2021-12-30 11:43:04', NULL, 2);
INSERT INTO `t_course` VALUES (47, 'JVM', 60, 30, '#1c22b0', '<p>JVM调优</p>', 5, '无限制', 5, 2, '2021-12-30 11:44:52', NULL, 2);
INSERT INTO `t_course` VALUES (48, 'Shiro', 45, 20, '#b7c41b', '<p>Shiro基本使用</p>', 2, '女', 5, 2, '2021-12-30 11:46:30', NULL, 2);
INSERT INTO `t_course` VALUES (49, 'Nacos', 50, 25, '#54d916', '<p>nacos服务注册&amp;发现与配置管理</p>', 4, '男', 11, 2, '2021-12-30 11:48:20', NULL, 2);
INSERT INTO `t_course` VALUES (50, 'Sentinel', 45, 35, '#db2c14', '<p>限流</p>', 3, '无限制', 5, 2, '2021-12-30 11:49:26', NULL, 2);

-- ----------------------------
-- Table structure for t_course_card
-- ----------------------------
DROP TABLE IF EXISTS `t_course_card`;
CREATE TABLE `t_course_card`  (
  `card_id` bigint(20) UNSIGNED NOT NULL COMMENT '会员卡id',
  `course_id` bigint(20) UNSIGNED NOT NULL COMMENT '课程id',
  PRIMARY KEY (`card_id`, `course_id`) USING BTREE,
  INDEX `fk_course_course_id`(`course_id`) USING BTREE,
  CONSTRAINT `fk_course_card_id` FOREIGN KEY (`card_id`) REFERENCES `t_member_card` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_course_course_id` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '中间表：课程-会员卡' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_course_card
-- ----------------------------
INSERT INTO `t_course_card` VALUES (45, 42);
INSERT INTO `t_course_card` VALUES (46, 42);
INSERT INTO `t_course_card` VALUES (48, 42);
INSERT INTO `t_course_card` VALUES (50, 42);
INSERT INTO `t_course_card` VALUES (51, 42);
INSERT INTO `t_course_card` VALUES (45, 43);
INSERT INTO `t_course_card` VALUES (46, 43);
INSERT INTO `t_course_card` VALUES (48, 43);
INSERT INTO `t_course_card` VALUES (49, 43);
INSERT INTO `t_course_card` VALUES (50, 43);
INSERT INTO `t_course_card` VALUES (51, 43);
INSERT INTO `t_course_card` VALUES (45, 44);
INSERT INTO `t_course_card` VALUES (46, 44);
INSERT INTO `t_course_card` VALUES (47, 44);
INSERT INTO `t_course_card` VALUES (48, 44);
INSERT INTO `t_course_card` VALUES (50, 44);
INSERT INTO `t_course_card` VALUES (51, 44);
INSERT INTO `t_course_card` VALUES (45, 45);
INSERT INTO `t_course_card` VALUES (48, 45);
INSERT INTO `t_course_card` VALUES (50, 45);
INSERT INTO `t_course_card` VALUES (51, 45);
INSERT INTO `t_course_card` VALUES (45, 46);
INSERT INTO `t_course_card` VALUES (48, 46);
INSERT INTO `t_course_card` VALUES (50, 46);
INSERT INTO `t_course_card` VALUES (51, 46);
INSERT INTO `t_course_card` VALUES (48, 47);
INSERT INTO `t_course_card` VALUES (50, 47);
INSERT INTO `t_course_card` VALUES (51, 47);
INSERT INTO `t_course_card` VALUES (48, 48);
INSERT INTO `t_course_card` VALUES (50, 48);
INSERT INTO `t_course_card` VALUES (51, 48);
INSERT INTO `t_course_card` VALUES (48, 49);
INSERT INTO `t_course_card` VALUES (50, 49);
INSERT INTO `t_course_card` VALUES (51, 49);
INSERT INTO `t_course_card` VALUES (48, 50);
INSERT INTO `t_course_card` VALUES (50, 50);
INSERT INTO `t_course_card` VALUES (51, 50);

-- ----------------------------
-- Table structure for t_employee
-- ----------------------------
DROP TABLE IF EXISTS `t_employee`;
CREATE TABLE `t_employee`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用来登录',
  `sex` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `introduce` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '介绍',
  `avatar_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像文件路径',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作角色名，暂不使用',
  `role_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作角色密码',
  `role_type` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '操作角色类型，1，超级管理员；0，普通管理员',
  `role_email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作角色邮箱',
  `is_deleted` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除，0有效，1无效',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_employee
-- ----------------------------
INSERT INTO `t_employee` VALUES (1, '张老师', '123456', '女', '2020-09-07', '骨干教师', '6523886b-4b0c-43d9-8029-14e08eae3d39.jpg', '教数学', 'admin', '567', 1, '3496351038@qq.com', 0, '2020-09-03 10:22:27', '2020-10-01 06:59:49', 1);
INSERT INTO `t_employee` VALUES (2, '李老', '123123', '男', '2020-09-09', '骨干教师', 'a5ef390d-e5ee-484b-9512-3850a415f108.jpg', '地理', '普通管理员', '11', 0, '3496351038@qq.com', 0, '2020-09-02 14:57:11', '2020-10-18 02:44:40', 1);
INSERT INTO `t_employee` VALUES (3, '黑衣人', '112358', '男', '2020-09-25', '骨干教师', '6689c2c3-fafd-4ecc-8feb-483cba0cb354.jpg', '玄学', 'test', '111', 0, '3496351038@qq.com', 0, '2020-12-23 13:32:15', NULL, 1);
INSERT INTO `t_employee` VALUES (4, '魏老', '4', '男', '2020-09-26', '骨干教师', 'a3cc73e2-3ec8-45ba-acd5-f454bafcc993.jpg', '教体育', 'user1', '123', 0, '3496351038@qq.com', 0, '2020-09-27 11:32:57', NULL, 1);
INSERT INTO `t_employee` VALUES (8, '周老', '8', '男', '2021-12-13', '骨干教师', '331f62df-69e3-498a-be23-8187674b1d28.jpg', '教体育', 'user5', '123', 0, '3496351038@qq.com', 0, '2020-09-27 11:32:57', NULL, 1);
INSERT INTO `t_employee` VALUES (9, '吴老', '92', '男', '1995-02-02', '骨干教师', '37be0fad-29d3-4007-b602-c01316aa2635.jpg', '教体育', '普通管理员', '123', 0, '3496351038@qq.com', 0, '2020-09-27 11:32:57', '2020-10-17 10:23:35', 1);
INSERT INTO `t_employee` VALUES (15, 'admin', '1231234', '女', '2000-02-02', '测试老师介绍', '53c3cbda-4ff4-4279-8ad4-5fe33443fc72.jpg', '测试老师备注', NULL, 'admin', 0, 'test@126.com', 0, '2021-12-30 12:42:08', NULL, 1);
INSERT INTO `t_employee` VALUES (16, 'test', '1111', '女', '2000-02-02', '简要好好', 'b6da337d-d929-477f-bfb9-99da0217ff4d.jpg', '那你', NULL, '123', 0, 'test@126.com', 1, '2021-12-30 14:53:40', NULL, 1);

-- ----------------------------
-- Table structure for t_global_reservation_set
-- ----------------------------
DROP TABLE IF EXISTS `t_global_reservation_set`;
CREATE TABLE `t_global_reservation_set`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `start_day` int(10) NULL DEFAULT NULL COMMENT '可提前预约的天数',
  `end_day` int(10) NULL DEFAULT NULL COMMENT '模式1：提前预约截止天数，上课前',
  `end_time` time(0) NULL DEFAULT NULL COMMENT '模式1：提前预约截止时间(24小时内)，上课前',
  `end_hour` int(10) NULL DEFAULT NULL COMMENT '模式2：提前预约截止小时数，离上课前',
  `cancel_day` int(10) NULL DEFAULT NULL COMMENT '模式1：提前预约取消的距离天数',
  `cancel_time` time(0) NULL DEFAULT NULL COMMENT '模式1：提前预约取消的时间限制（24小时内）',
  `cancel_hour` int(10) NULL DEFAULT NULL COMMENT '模式2：提前预约取消的距离小时数',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  `appointment_start_mode` int(1) NOT NULL DEFAULT 1 COMMENT '预约开始时间的模式，1：不限制会员可提前预约天数；2：限制天数',
  `appointment_deadline_mode` int(1) NOT NULL DEFAULT 1 COMMENT '预约截止时间模式；1：不限制截止时间；2：限制为上课前xx小时；3：限制为上课前xx天xx：xx（时间点）',
  `appointment_cancel_mode` int(1) NOT NULL DEFAULT 1 COMMENT '预约取消时间模式；1：不限制取消时间（上课前都可取消）；2：上课前xx小时可取消；3：上课前xx天xx：xx可取消',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '全局预约设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_global_reservation_set
-- ----------------------------
INSERT INTO `t_global_reservation_set` VALUES (1, -1, 1, '17:30:00', 2, 1, '12:00:00', 2, '2020-10-21 17:02:12', '2020-10-21 18:06:28', 17, 2, 2, 3);

-- ----------------------------
-- Table structure for t_member
-- ----------------------------
DROP TABLE IF EXISTS `t_member`;
CREATE TABLE `t_member`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sex` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `avatar_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像路径',
  `is_deleted` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '用户的逻辑删除，0有效，1无效',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_member
-- ----------------------------
INSERT INTO `t_member` VALUES (85, '萧炎', '男', '18899996666', '2000-02-02', '我是萧炎', 'c2249f86-9189-4fc4-9879-717841db5c2e.webp', 0, '2021-12-30 13:45:56', '2021-12-30 14:50:33', 3);
INSERT INTO `t_member` VALUES (86, '林动', '男', '17788889999', '1991-03-01', '我是林动', 'd707e60e-4ef5-4097-8854-1d3b8bdf0fad.jpg', 0, '2021-12-30 13:53:52', '2021-12-30 14:22:23', 2);
INSERT INTO `t_member` VALUES (87, '叶凡', '男', '19999999999', '2000-02-02', '我是叶凡', NULL, 0, '2021-12-30 13:54:31', NULL, 1);
INSERT INTO `t_member` VALUES (88, '楚南', '男', '14455666655', '2000-02-04', '我是楚风', '83cea4bd-b2af-459d-841c-01a99ad91445.jpg', 0, '2021-12-30 13:55:55', '2021-12-30 14:49:53', 2);
INSERT INTO `t_member` VALUES (89, '赵灵儿', '女', '17777777777', '2003-01-29', '我是赵灵儿', 'e9e5c930-c79f-48e3-9630-5245e2f518c0.jpg', 0, '2021-12-30 13:56:20', '2021-12-30 14:50:50', 2);
INSERT INTO `t_member` VALUES (90, '任盈盈', '女', '13355666655', '2001-03-02', '我是任盈盈', '8d5eee0e-2ccc-4757-ad8b-52f42c89775a.jpg', 0, '2021-12-30 14:02:55', '2021-12-30 14:41:16', 2);

-- ----------------------------
-- Table structure for t_member_bind_record
-- ----------------------------
DROP TABLE IF EXISTS `t_member_bind_record`;
CREATE TABLE `t_member_bind_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `card_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `valid_count` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '可使用次数',
  `valid_day` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '有效期，按天算',
  `received_money` decimal(10, 2) NULL COMMENT '实收金额',
  `pay_mode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `active_status` tinyint(1) UNSIGNED NULL DEFAULT 1 COMMENT '激活状态，1激活，0非激活',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_bind_member_id`(`member_id`) USING BTREE,
  INDEX `fk_bind_card_id`(`card_id`) USING BTREE,
  CONSTRAINT `fk_bind_card_id` FOREIGN KEY (`card_id`) REFERENCES `t_member_card` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_bind_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '中间表：会员绑定记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_member_bind_record
-- ----------------------------
INSERT INTO `t_member_bind_record` VALUES (24, 85, 51, 60, 37, 200.00, '银行卡', '萧炎办了一张金卡', 1, '2021-12-30 13:46:44', NULL, 1);
INSERT INTO `t_member_bind_record` VALUES (25, 87, 51, 60, 37, 99.00, '银行卡', '叶凡办了至尊卡', 1, '2021-12-30 13:54:54', NULL, 1);
INSERT INTO `t_member_bind_record` VALUES (26, 89, 51, 60, 37, 55.00, '银行卡', '我是赵灵儿', 1, '2021-12-30 13:56:39', NULL, 1);
INSERT INTO `t_member_bind_record` VALUES (27, 90, 51, 60, 37, 55.00, '银行卡', '任盈盈办了一张银卡', 1, '2021-12-30 14:03:19', NULL, 1);
INSERT INTO `t_member_bind_record` VALUES (28, 86, 51, 60, 37, 100.00, '银行卡', '.。', 1, '2021-12-30 14:37:08', NULL, 1);
INSERT INTO `t_member_bind_record` VALUES (29, 86, 45, 1049, 1029, 100.00, '银行卡', '林动又绑定了一张金卡', 1, '2021-12-30 14:39:24', NULL, 1);

-- ----------------------------
-- Table structure for t_member_card
-- ----------------------------
DROP TABLE IF EXISTS `t_member_card`;
CREATE TABLE `t_member_card`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) UNSIGNED NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述信息',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员卡类型',
  `total_count` int(10) UNSIGNED NULL DEFAULT 24 COMMENT '默认可用次数',
  `total_day` int(10) UNSIGNED NULL DEFAULT 7 COMMENT '默认可用天数',
  `status` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '激活状态，0激活，1非激活',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_member_card
-- ----------------------------
INSERT INTO `t_member_card` VALUES (45, '金卡', 999.00, '金卡可听所有普通课程', '这是金卡', '次卡(无期限)', 999, 999, 0, '2021-12-30 13:02:56', NULL, 1);
INSERT INTO `t_member_card` VALUES (46, '银卡', 888.00, '银卡可听部分课程', '这是银卡', '次卡(无期限)', 555, 100, 0, '2021-12-30 13:27:32', NULL, 1);
INSERT INTO `t_member_card` VALUES (47, '铜卡', 666.00, '铜卡只能听一种课程', '这是铜卡', '次卡(无期限)', 100, 100, 0, '2021-12-30 13:28:53', NULL, 1);
INSERT INTO `t_member_card` VALUES (48, '至尊卡', 99999.00, '至尊卡可以无限期畅听所有课程', '这是至尊卡', '次卡(无期限)', 9999999, 9999999, 0, '2021-12-30 13:30:00', NULL, 1);
INSERT INTO `t_member_card` VALUES (49, '临时卡', 100.00, '临时生效卡', '这是临时卡', '次卡(有期限)', 5, 1, 0, '2021-12-30 13:30:55', NULL, 1);
INSERT INTO `t_member_card` VALUES (50, '体验卡', 50.00, '体验卡可以体验一次所有课程', '这是体验卡', '次卡(有期限)', 5, 7, 0, '2021-12-30 13:31:44', NULL, 1);
INSERT INTO `t_member_card` VALUES (51, '测试卡', 1.00, '测试卡', '这是测试卡', '次卡(有期限)', 10, 7, 0, '2021-12-30 13:32:23', NULL, 1);

-- ----------------------------
-- Table structure for t_member_log
-- ----------------------------
DROP TABLE IF EXISTS `t_member_log`;
CREATE TABLE `t_member_log`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作类型',
  `involve_money` decimal(10, 2) NULL COMMENT '影响的金额',
  `operator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作员名称',
  `member_bind_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '会员绑定id',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `last_modify_time` datetime(0) NULL DEFAULT NULL,
  `version` int(10) UNSIGNED NULL DEFAULT 1,
  `card_count_change` int(11) NULL DEFAULT 0 COMMENT '单个操作的卡次变化',
  `card_day_change` int(11) NULL DEFAULT 0 COMMENT '单个操作的天次变化',
  `note` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_log_member_bind_id`(`member_bind_id`) USING BTREE,
  CONSTRAINT `fk_log_member_bind_id` FOREIGN KEY (`member_bind_id`) REFERENCES `t_member_bind_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '操作记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_member_log
-- ----------------------------
INSERT INTO `t_member_log` VALUES (90, '绑卡操作', 1.00, '张老师', 24, '2021-12-30 13:46:44', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (91, '绑卡充值操作', 200.00, '张老师', 24, '2021-12-30 13:46:44', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (92, '绑卡操作', 1.00, '张老师', 25, '2021-12-30 13:54:54', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (93, '绑卡充值操作', 99.00, '张老师', 25, '2021-12-30 13:54:54', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (94, '绑卡操作', 1.00, '张老师', 26, '2021-12-30 13:56:39', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (95, '绑卡充值操作', 55.00, '张老师', 26, '2021-12-30 13:56:39', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (96, '绑卡操作', 1.00, '张老师', 27, '2021-12-30 14:03:19', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (97, '绑卡充值操作', 55.00, '张老师', 27, '2021-12-30 14:03:19', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (98, '绑卡操作', 1.00, '张老师', 28, '2021-12-30 14:37:08', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (99, '绑卡充值操作', 100.00, '张老师', 28, '2021-12-30 14:37:08', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (100, '绑卡操作', 999.00, '张老师', 29, '2021-12-30 14:39:24', NULL, 1, 0, 0, NULL);
INSERT INTO `t_member_log` VALUES (101, '绑卡充值操作', 100.00, '张老师', 29, '2021-12-30 14:39:24', NULL, 1, 0, 0, NULL);

-- ----------------------------
-- Table structure for t_recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `t_recharge_record`;
CREATE TABLE `t_recharge_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `add_count` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '充值可用次数',
  `add_day` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '延长有效天数',
  `received_money` decimal(10, 2) UNSIGNED NULL COMMENT '实收金额',
  `pay_mode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `operator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作员',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `member_bind_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '会员绑定id',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `last_modify_time` datetime(0) NULL DEFAULT NULL,
  `version` int(10) UNSIGNED NULL DEFAULT 1,
  `log_Id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '操作记录id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_charge_member_bind_id`(`member_bind_id`) USING BTREE,
  INDEX `t_recharge_record_t_member_log_id_fk`(`log_Id`) USING BTREE,
  CONSTRAINT `fk_charge_member_bind_id` FOREIGN KEY (`member_bind_id`) REFERENCES `t_member_bind_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_recharge_record_t_member_log_id_fk` FOREIGN KEY (`log_Id`) REFERENCES `t_member_log` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '充值记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_recharge_record
-- ----------------------------
INSERT INTO `t_recharge_record` VALUES (54, 10, 7, 1.00, '银行卡', '张老师', '这是测试卡', 24, '2021-12-30 13:46:44', NULL, 1, 90);
INSERT INTO `t_recharge_record` VALUES (55, 50, 30, 200.00, '银行卡', '张老师', '萧炎办了一张金卡', 24, '2021-12-30 13:46:44', NULL, 1, 91);
INSERT INTO `t_recharge_record` VALUES (56, 10, 7, 1.00, '银行卡', '张老师', '这是测试卡', 25, '2021-12-30 13:54:54', NULL, 1, 92);
INSERT INTO `t_recharge_record` VALUES (57, 50, 30, 99.00, '银行卡', '张老师', '叶凡办了至尊卡', 25, '2021-12-30 13:54:54', NULL, 1, 93);
INSERT INTO `t_recharge_record` VALUES (58, 10, 7, 1.00, '银行卡', '张老师', '这是测试卡', 26, '2021-12-30 13:56:39', NULL, 1, 94);
INSERT INTO `t_recharge_record` VALUES (59, 50, 30, 55.00, '银行卡', '张老师', '我是赵灵儿', 26, '2021-12-30 13:56:39', NULL, 1, 95);
INSERT INTO `t_recharge_record` VALUES (60, 10, 7, 1.00, '银行卡', '张老师', '这是测试卡', 27, '2021-12-30 14:03:19', NULL, 1, 96);
INSERT INTO `t_recharge_record` VALUES (61, 50, 30, 55.00, '银行卡', '张老师', '任盈盈办了一张银卡', 27, '2021-12-30 14:03:19', NULL, 1, 97);
INSERT INTO `t_recharge_record` VALUES (62, 10, 7, 1.00, '银行卡', '张老师', '这是测试卡', 28, '2021-12-30 14:37:08', NULL, 1, 98);
INSERT INTO `t_recharge_record` VALUES (63, 50, 30, 100.00, '银行卡', '张老师', '.。', 28, '2021-12-30 14:37:08', NULL, 1, 99);
INSERT INTO `t_recharge_record` VALUES (64, 999, 999, 999.00, '银行卡', '张老师', '这是金卡', 29, '2021-12-30 14:39:24', NULL, 1, 100);
INSERT INTO `t_recharge_record` VALUES (65, 50, 30, 100.00, '银行卡', '张老师', '林动又绑定了一张金卡', 29, '2021-12-30 14:39:24', NULL, 1, 101);

-- ----------------------------
-- Table structure for t_reservation_record
-- ----------------------------
DROP TABLE IF EXISTS `t_reservation_record`;
CREATE TABLE `t_reservation_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '预约状态，1有效，0无效',
  `reserve_nums` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '单次操作预约人数',
  `cancel_times` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '取消次数统计',
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '教师评语',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `class_note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上课备注',
  `operator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作员',
  `member_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '会员id',
  `card_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员指定的会员卡来预约',
  `schedule_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '排课记录id',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `last_modify_time` datetime(0) NULL DEFAULT NULL,
  `version` int(10) UNSIGNED NULL DEFAULT 1,
  `card_id` int(11) NULL DEFAULT NULL COMMENT '会员卡id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_reserve_member_id`(`member_id`) USING BTREE,
  INDEX `fk_reserve_schedule_id`(`schedule_id`) USING BTREE,
  CONSTRAINT `fk_reserve_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reserve_schedule_id` FOREIGN KEY (`schedule_id`) REFERENCES `t_schedule_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '预约记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_reservation_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_schedule_record
-- ----------------------------
DROP TABLE IF EXISTS `t_schedule_record`;
CREATE TABLE `t_schedule_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `course_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '课程号',
  `teacher_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '教师号',
  `order_nums` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '此项排课的预约人数',
  `start_date` date NULL DEFAULT NULL COMMENT '上课日期',
  `class_time` time(0) NULL DEFAULT NULL COMMENT '上课时间',
  `limit_sex` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '限制性别',
  `limit_age` int(10) NULL DEFAULT NULL COMMENT '限制年龄',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `only_course_teach`(`course_id`, `teacher_id`, `class_time`, `start_date`) USING BTREE,
  INDEX `fk_sche_teacher_id`(`teacher_id`) USING BTREE,
  CONSTRAINT `fk_sche_course_id` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sche_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `t_employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 89 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '中间表：排课计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_schedule_record
-- ----------------------------
INSERT INTO `t_schedule_record` VALUES (88, 42, 1, 0, '2021-12-30', '09:30:00', NULL, NULL, '2021-12-30 14:51:44', NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;
