package com.erhu1999.shopordertest.version014;

import com.erhu1999.shopordertest.common.Constant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.FutureTask;

import static com.erhu1999.shopordertest.common.AssertUtil.assertBoolIsTrue;
import static com.erhu1999.shopordertest.common.AssertUtil.assertNotNull;
import static com.erhu1999.shopordertest.version014.JdbcUtil014.getConnectionFromPool;

/**
 * {@link #DISPLAY_NAME}
 *
 * @author HuKaiXuan
 */
class Version014SeparateCommit {
  /** 显示名称（类注释），当前类与测试类共用一段描述 */
  public static final String DISPLAY_NAME = "第014版提交订单";

  /**
   * 提交订单
   *
   * @param userId     用户ID
   * @param goodsId    商品ID
   * @param goodsCount 商品数量
   * @param addrId     地址ID
   */
  public void submitOrder(Long userId, Long goodsId, int goodsCount, Long addrId) throws Exception {
    // 1、查询用户
    Map<String, Object> user = JdbcUtil014.queryOneRow("select `id`,`name` from `User` as t where t.id=" + userId);
    // 2、查询商品
    Map<String, Object> goods = JdbcUtil014.queryOneRow("select `id`,`name`,`price`,`stock`,`sales`,`isOn`,`firstImg` from `Goods` as t where t.id=" + goodsId);
    // 3、查询地址
    Map<String, Object> addr = JdbcUtil014.queryOneRow("select `id`,`userId`,`mobile`,`name`,`addr` from `Addr` as t where t.id=" + addrId);
    // 4、检查其他参数
    checkParam(userId, goodsId, goodsCount, addrId, user, goods, addr);
    // 5、保存订单到数据库中，并返回订单ID
    FutureTask<Long> futureTask = new FutureTask(() -> saveOrder(userId, goodsCount, user, goods, addr));
    new Thread(futureTask).start();
    // 7、更新商品的库存与销量
    updateGoodsStockAndSales(goodsId, goodsCount, goods);
    // 6、保存订单商品到数据库中
    saveOrderGoods(goodsId, goodsCount, goods, futureTask.get());
  }

  /** 更新商品的库存与销量 */
  private void updateGoodsStockAndSales(Long goodsId, int goodsCount, Map<String, Object> goods) throws SQLException {
    StringBuilder updateSqlBuilder = new StringBuilder("update `Goods` set `stock` = `stock` - ")
      .append(goodsCount).append(" ").append(", `sales` = `sales` + ").append(goodsCount).append(" ")
      .append("where `id` = ").append(goodsId).append(" and (`stock` - ").append(goodsCount).append(") >= 0");
    String updateSql = updateSqlBuilder.toString();
    int effectRowCount = executeSql(updateSql);
    if (effectRowCount == 0) {
      throw new RuntimeException("商品库存不足，无法购买：" + goods.get("name"));
    }
  }

  /** 执行Sql，返回影响的行数 */
  private int executeSql(String updateSql) throws SQLException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnectionFromPool();
      stmt = conn.createStatement();
      return stmt.executeUpdate(updateSql);
    } finally {
      JdbcUtil014.close(conn, stmt, null, rs);
    }
  }

  /** 保存订单商品 */
  private void saveOrderGoods(Long goodsId, int goodsCount, Map<String, Object> goods, long orderId) throws SQLException {
    // 商品单价
    long price = (Long) goods.get("price");
    String baseSql = "INSERT INTO `OrderGoods`(`orderId`, `goodsId`, `goodsCount`, `goodsPrice`, `goodsImg`, `money`) VALUES (";
    StringBuilder insertSqlBuilder = new StringBuilder(baseSql)
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
    executeSql(insertSql);
  }

  /** 保存订单 */
  private long saveOrder(Long userId, int goodsCount, Map<String, Object> user, Map<String, Object> goods, Map<String, Object> addr) throws Exception {
    // 商品单价
    long price = (Long) goods.get("price");
    String baseSql = "INSERT INTO `Order`(`sn`,`userId`,`name`,`addrMobile`,`addrName`,`addrDetail`,`money`,`status`,`addTime`) VALUES (";
    StringBuilder insertSqlBuilder = new StringBuilder(baseSql)
      // 编号（惟一）
      .append("'").append(Math.abs(UUID.randomUUID().hashCode())).append("'").append(",")
      // 用户ID
      .append(userId).append(",")
      // 下单人姓名
      .append("'").append(user.get("name")).append("'").append(",")
      // 收货地址中的手机号
      .append("'").append(addr.get("mobile")).append("'").append(",")
      // 收货地址中的姓名
      .append("'").append(addr.get("name")).append("'").append(",")
      // 收货地址
      .append("'").append(addr.get("addr")).append("'").append(",")
      // 订单金额（单位为分）
      .append(goodsCount * price).append(",")
      // 状态
      .append(Constant.ORDER_STATUS_WAIT_PAY).append(",")
      // 下单时间
      .append(System.currentTimeMillis())
      .append(")");
    String insertSql = insertSqlBuilder.toString();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnectionFromPool();
      stmt = conn.createStatement();
      stmt.executeUpdate(insertSql, Statement.RETURN_GENERATED_KEYS);
      rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        long orderId = rs.getLong(1);
        return orderId;
      }
    } finally {
      JdbcUtil014.close(conn, stmt, null, rs);
    }
    throw new RuntimeException("添加订单失败");
  }

  /** 检查参数 */
  private void checkParam(Long userId, Long goodsId, int goodsCount, Long addrId, Map<String, Object> user, Map<String, Object> goods, Map<String, Object> addr) {
    assertNotNull(userId, "用户ID不允许为空：userId");
    assertNotNull(goodsId, "商品ID不允许为空：goodsId");
    assertBoolIsTrue(goodsCount > 0, "商品数量不允许小于零：goodsCount");
    assertNotNull(addrId, "地址ID不允许为空：addrId");
    assertNotNull(user, "用户不存在：" + userId);
    assertNotNull(goods, "商品不存在：" + goodsId);
    assertNotNull(addr, "地址不存在：" + addrId);
    Long addrUserId = (Long) addr.get("userId");
    assertBoolIsTrue(userId.longValue() == addrUserId.longValue(), "不允许使用其他用户的地址");
    // 商品是否上架（1是0否）
    Integer isOn = (Integer) goods.get("isOn");
    assertBoolIsTrue(isOn.intValue() == 1, "商品已下架，不允许购买");
    // 商品库存是否充足
    Integer stock = (Integer) goods.get("stock");
    assertBoolIsTrue(stock.intValue() >= goodsCount, "商品库存不足，无法购买：" + goods.get("name"));
  }

}
