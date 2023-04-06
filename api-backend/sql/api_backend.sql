/*
 Navicat Premium Data Transfer

 Source Server         : localhost_mysql
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : api_backend

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 06/04/2023 16:06:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
                                   `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                                   `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口地址',
                                   `requestParams` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求参数',
                                   `requestHeader` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求头',
                                   `responseHeader` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应头',
                                   `status` int NOT NULL DEFAULT 0 COMMENT '接口状态（0-关闭，1-开启）',
                                   `method` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求类型',
                                   `userId` bigint NOT NULL COMMENT '创建人',
                                   `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '接口信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_info
-- ----------------------------
INSERT INTO `interface_info` VALUES (1, '测试GET-name', 'test', '/provider/name/get', '[{\"name\":\"username\",\"type\":\"string\"}]', '{\"Content-Type\":\"application/x-www-form-urlencoded\",\"Accept\":\"*/*\"}', '{}', 0, 'GET', 2, '2023-04-06 16:02:38', '2023-04-06 16:02:38', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
                         `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
                         `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
                         `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
                         `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
                         `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                         `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE,
                         UNIQUE INDEX `uni_userAccount`(`userAccount`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, NULL, 'root', NULL, NULL, 'user', 'cfbe7cd8228cb3c997885b6a76e220f8', '2023-04-06 16:02:20', '2023-04-06 16:02:20', 0);

-- ----------------------------
-- Table structure for user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_info`;
CREATE TABLE `user_interface_info`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `userId` bigint NOT NULL COMMENT '调用用户 id',
                                        `interfaceInfoId` bigint NOT NULL COMMENT '接口 id',
                                        `totalNum` int NOT NULL DEFAULT 0 COMMENT '总调用次数',
                                        `leftNum` int NOT NULL DEFAULT 0 COMMENT '剩余调用次数',
                                        `status` int NOT NULL DEFAULT 0 COMMENT '0-正常，1-禁用',
                                        `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户调用接口关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_interface_info
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
