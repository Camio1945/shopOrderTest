SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
 -- ----------------------------
-- Table structure for Addr
-- ----------------------------
DROP TABLE IF EXISTS `Addr`;
CREATE TABLE `Addr`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '手机',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '姓名',
  `addr` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '地址' ROW_FORMAT = Dynamic;
 -- ----------------------------
-- Records of Addr
-- ----------------------------
INSERT INTO `Addr` VALUES (1, 1, '18812345678', '张三', '北京市海淀区一个假的收货地址');
 -- ----------------------------
-- Table structure for Goods
-- ----------------------------
DROP TABLE IF EXISTS `Goods`;
CREATE TABLE `Goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `price` bigint(20) NOT NULL DEFAULT 0 COMMENT '销售价（单位为分）',
  `stock` int(10) NOT NULL DEFAULT 0 COMMENT '库存',
  `sales` int(10) NOT NULL DEFAULT 0 COMMENT '销量',
  `isOn` int(1) NOT NULL DEFAULT 1 COMMENT '是否上架',
  `firstImg` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第一张图片',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详情',
  `addTime` bigint(20) NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品' ROW_FORMAT = Dynamic;
 -- ----------------------------
-- Records of Goods
-- ----------------------------
INSERT INTO `Goods` VALUES (1, '锤子（smartisan ) 坚果 Pro 2S 6G+64GB 炫光蓝 全面屏双摄 全网通4G手机 双卡双待 游戏手机', 199800, 320, 0, 1, 'https://resource.smartisan.com/resource/25cc6e783a664fbdf83c3c34774a9826.png', 'https://resource.smartisan.com/resource/25cc6e783a664fbdf83c3c34774a9826.png', 20180901115333000);
 -- ----------------------------
-- Table structure for Order
-- ----------------------------
DROP TABLE IF EXISTS `Order`;
CREATE TABLE `Order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sn` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '编号',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '下单人姓名',
  `addrMobile` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '收货地址中的手机号',
  `addrName` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '收货地址中的姓名',
  `addrDetail` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '收货地址',
  `money` bigint(20) NOT NULL COMMENT '订单金额（单位为分）',
  `status` int(1) NOT NULL COMMENT '状态：1-待支付，2-待发货，3-待收货，4-已完成，5-已取消',
  `addTime` bigint(20) NOT NULL COMMENT '下单时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '订单' ROW_FORMAT = Dynamic;
 -- ----------------------------
-- Table structure for OrderGoods
-- ----------------------------
DROP TABLE IF EXISTS `OrderGoods`;
CREATE TABLE `OrderGoods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orderId` bigint(20) NOT NULL COMMENT '订单ID',
  `goodsId` bigint(20) NOT NULL COMMENT '商品ID',
  `goodsCount` int(8) NOT NULL COMMENT '商品数量',
  `goodsPrice` bigint(20) NOT NULL COMMENT '商品单价（单位为分）',
  `goodsImg` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '商品图片',
  `money` bigint(20) NOT NULL COMMENT '商品金额（单位为分）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '订单商品' ROW_FORMAT = Dynamic;
 -- ----------------------------
-- Table structure for User
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '姓名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户' ROW_FORMAT = Dynamic;
 -- ----------------------------
-- Records of User
-- ----------------------------
INSERT INTO `User` VALUES (1, '张三');
 SET FOREIGN_KEY_CHECKS = 1;