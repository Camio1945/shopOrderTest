package com.erhu1999.shopordertest.version017;

import com.erhu1999.shopordertest.common.Constant;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.FutureTask;

import static com.erhu1999.shopordertest.common.AssertUtil.assertBoolIsTrue;
import static com.erhu1999.shopordertest.common.AssertUtil.assertNotNull;

/**
 * {@link #DISPLAY_NAME}
 *
 * @author HuKaiXuan
 */
class Version017StringId {
  /** 显示名称（类注释），当前类与测试类共用一段描述 */
  public static final String DISPLAY_NAME = "第017版提交订单 生成36位的String类型的ID";

  /**
   * 提交订单
   *
   * @param userId     用户ID
   * @param goodsId    商品ID
   * @param goodsCount 商品数量
   * @param addrMobile 收货地址中的手机号
   * @param addrName   收货地址中的姓名
   * @param addrDetail 收货地址详情
   */
  public void submitOrder(String userId, String goodsId, int goodsCount, String addrMobile, String addrName, String addrDetail) throws Exception {
    // 1、查询用户
    Map<String, Object> user = JdbcUtil017.queryOneRow("select `id`,`name` from `User` as t where t.id=" + userId);
    // 2、查询商品
    Map<String, Object> goods = JdbcUtil017.queryOneRow("select `id`,`name`,`price`,`stock`,`sales`,`isOn`,`firstImg` from `Goods` as t where t.id=" + goodsId);
    // 4、检查其他参数
    checkParam(userId, goodsId, goodsCount, user, goods, addrMobile, addrName, addrDetail);
    Connection conn = JdbcUtil017.getConnectionFromPool();
    conn.setAutoCommit(false);
    try {
      // 5、保存订单到数据库中，并返回订单ID
      FutureTask<String> futureTask = new FutureTask(() -> saveOrder(conn, userId, goodsCount, user, goods, addrMobile, addrName, addrDetail));
      new Thread(futureTask).start();
      // 7、更新商品的库存与销量
      updateGoodsStockAndSales(conn, goodsId, goodsCount, goods);
      // 6、保存订单商品到数据库中
      saveOrderGoods(conn, goodsId, goodsCount, goods, futureTask.get());
      conn.commit();
    } catch (Exception e) {
      conn.rollback();
      throw e;
    }
  }

  /** 检查参数 */
  private void checkParam(String userId, String goodsId, int goodsCount, Map<String, Object> user, Map<String, Object> goods, String addrMobile, String addrName, String addrDetail) {
    assertNotNull(userId, "用户ID不允许为空：userId");
    assertNotNull(goodsId, "商品ID不允许为空：goodsId");
    assertBoolIsTrue(goodsCount > 0, "商品数量不允许小于零：goodsCount");
    assertNotNull(addrMobile, "收货地址中的手机号不允许为空：addrMobile");
    assertNotNull(addrName, "收货地址中的姓名不允许为空：addrName");
    assertNotNull(addrDetail, "收货地址不允许为空：addrDetail");
    assertNotNull(user, "用户不存在：" + userId);
    assertNotNull(goods, "商品不存在：" + goodsId);
    // 商品是否上架（1是0否）
    Integer isOn = (Integer) goods.get("isOn");
    assertBoolIsTrue(isOn.intValue() == 1, "商品已下架，不允许购买");
  }

  /** 保存订单 */
  private String saveOrder(Connection conn, String userId, int goodsCount, Map<String, Object> user, Map<String, Object> goods, String addrMobile, String addrName, String addrDetail) throws Exception {
    // 商品单价
    Long price = (Long) goods.get("price");
    String baseSql = "INSERT INTO `Order`(`id`,`sn`,`userId`,`name`,`addrMobile`,`addrName`,`addrDetail`,`money`,`status`,`addTime`) VALUES (";
    UUID orderId = UUID.randomUUID();
    StringBuilder insertSqlBuilder = new StringBuilder(baseSql)
        // ID
        .append("'").append(orderId).append("'").append(",")
        // 编号（惟一）
        .append("'").append(Math.abs(orderId.hashCode())).append("'").append(",")
        // 用户ID
        .append(userId).append(",")
        // 下单人姓名
        .append("'").append(user.get("name")).append("'").append(",")
        // 收货地址中的手机号
        .append("'").append(addrMobile).append("'").append(",")
        // 收货地址中的姓名
        .append("'").append(addrName).append("'").append(",")
        // 收货地址
        .append("'").append(addrDetail).append("'").append(",")
        // 订单金额（单位为分）
        .append(goodsCount * price).append(",")
        // 状态
        .append(Constant.ORDER_STATUS_WAIT_PAY).append(",")
        // 下单时间
        .append(System.currentTimeMillis())
        .append(")");
    String insertSql = insertSqlBuilder.toString();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      int affectedRows = stmt.executeUpdate(insertSql, Statement.RETURN_GENERATED_KEYS);
      if (affectedRows <= 0) {
        throw new RuntimeException("添加订单失败");
      }
      return "'" + orderId + "'";
    } finally {
      JdbcUtil017.close(null, stmt, null, rs);
    }
  }

  /** 更新商品的库存与销量 */
  private void updateGoodsStockAndSales(Connection conn, String goodsId, int goodsCount, Map<String, Object> goods) throws SQLException {
    StringBuilder updateSqlBuilder = new StringBuilder("update `Goods` set `stock` = `stock` - ")
        .append(goodsCount).append(" ").append(", `sales` = `sales` + ").append(goodsCount).append(" ")
        .append("where `id` = ").append(goodsId).append(" and (`stock` - ").append(goodsCount).append(") >= 0");
    String updateSql = updateSqlBuilder.toString();
    int effectRowCount = executeSql(conn, updateSql);
    if (effectRowCount == 0) {
      throw new RuntimeException("商品库存不足，无法购买：" + goods.get("name"));
    }
  }

  /** 保存订单商品 */
  private void saveOrderGoods(Connection conn, String goodsId, int goodsCount, Map<String, Object> goods, String orderId) throws SQLException {
    // 商品单价
    Long price = (Long) goods.get("price");
    String baseSql = "INSERT INTO `OrderGoods`(`id`, `orderId`, `goodsId`, `goodsCount`, `goodsPrice`, `goodsImg`, `money`) VALUES (";
    String uuid = UUID.randomUUID().toString();
    StringBuilder insertSqlBuilder = new StringBuilder(baseSql)
        // ID
        .append("'").append(uuid).append("'").append(",")
        // 订单ID
        .append(orderId).append(",")
        // 商品ID
        .append(goodsId).append(",")
        // 商品数量
        .append(goodsCount).append(",")
        // 商品单价（单位为分）
        .append(price).append(",")
        // 商品图片
        .append("'").append(goods.get("firstImg")).append("'").append(",")
        // 商品总金额（单位为分）
        .append(price * goodsCount)
        .append(")");
    String insertSql = insertSqlBuilder.toString();
    executeSql(conn, insertSql);
  }

  /** 执行Sql，返回影响的行数 */
  private int executeSql(Connection conn, String updateSql) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      return stmt.executeUpdate(updateSql);
    } finally {
      JdbcUtil017.close(null, stmt, null, rs);
    }
  }

}
