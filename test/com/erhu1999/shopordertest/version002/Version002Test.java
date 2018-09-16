package com.erhu1999.shopordertest.version002;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.Map;

import static com.erhu1999.shopordertest.common.Config.DB_NAME_PREFIX;
import static com.erhu1999.shopordertest.common.Config.DB_PWD;
import static com.erhu1999.shopordertest.common.Config.DB_URL;
import static com.erhu1999.shopordertest.common.Config.DB_USER;
import static com.erhu1999.shopordertest.version002.JdbcUtil002.dropDbIfExistsThenCreateDb;
import static com.erhu1999.shopordertest.version002.JdbcUtil002.execSqlFile;
import static com.erhu1999.shopordertest.version002.JdbcUtil002.init;
import static com.erhu1999.shopordertest.version002.JdbcUtil002.newConnection;
import static com.erhu1999.shopordertest.version002.JdbcUtil002.queryOneRow;
import static com.erhu1999.shopordertest.version002.JdbcUtil002.renewUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 版本002的测试用例
 */
class Version002Test extends AbstractTest {
    /** 数据库名称 */
    private static final String DB_NAME = DB_NAME_PREFIX + "version002";
    /** 数据库连接数量 */
    private static final int CONN_NUMBER = 100;

    /** 在所有测试用例执行之前，先执行一遍初始化数据库的方法 */
    @BeforeAll
    static void initDb() {
        printSeparateLine(com.erhu1999.shopordertest.version002.Version002Test.class.getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        System.out.println("正在初始化数据库：" + DB_NAME);
        // 数据库数据库连接相关的参数
        init(DB_URL, DB_USER, DB_PWD);
        // 删除数据库（如果存在的话），然后创建数据库
        dropDbIfExistsThenCreateDb(DB_NAME);
        // 重新初始化数据库连接相关的参数
        renewUrl(DB_NAME);
        String sqlFilePath = com.erhu1999.shopordertest.version002.Version002Test.class.getResource("").getFile() + com.erhu1999.shopordertest.version002.Version002Test.class.getSimpleName() + ".sql";
        sqlFilePath = new File(sqlFilePath).getAbsolutePath().replaceAll("\\\\", "/");
        execSqlFile(sqlFilePath);
        System.out.println("初始化数据库完成：" + DB_NAME);
        LinkedList<Connection> connectionLinkedList = new LinkedList<>();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < CONN_NUMBER; i++) {
            Connection connection = newConnection();
            connectionLinkedList.add(connection);
        }
        long endTime = System.currentTimeMillis();
        long avgMillis = (endTime - startTime) / CONN_NUMBER;
        System.out.println("获取一个数据库连接平均耗时：" + avgMillis + " 毫秒");
        ConnectionPool002.initPool(connectionLinkedList);
        System.out.println("初始化数据库连接池完成，数量为：" + CONN_NUMBER);
    }

    @Test
    @DisplayName(Version002.DISPLAY_NAME + " 单线程测试")
    void testDataSourceByMe() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        // 商品ID
        Long goodsId = 1L;
        // 查询商品信息
        Map<String, Object> goods = queryOneRow("select `stock`,`sales` from `Goods` as t where t.id=" + goodsId);
        // 初始库存
        int initStock = (Integer) goods.get("stock");
        // 初始销量
        int initSales = (Integer) goods.get("sales");
        // 用户ID
        Long userId = 1L;
        // 购买商品数量
        int goodsCount = 1;
        // 地址ID
        Long addrId = 1L;
        // 提交订单的次数
        int submitCnt = 100;
        // 开始时间（毫秒）
        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < submitCnt; i++) {
            // 调用提交订单的接口
            Version002.submitOrder(userId, goodsId, goodsCount, addrId);
            if (i % 11 == 0) {
                System.out.println("当前循环的序号：" + i);
            }
        }
        // 结束时间（毫秒）
        long endTimeMillis = System.currentTimeMillis();
        // 平均时间（毫秒）
        long avgTimeMillis = (endTimeMillis - startTimeMillis) / submitCnt;
        System.out.println("提交每个订单平均耗时的毫秒数：" + avgTimeMillis);
        System.out.println("每秒钟可以提交的订单数：" + 1000 / avgTimeMillis);
        // 查询下单之后的商品信息
        goods = queryOneRow("select `stock`,`sales` from `Goods` as t where t.id=" + goodsId);
        // 下单之后的库存
        int afterStock = (Integer) goods.get("stock");
        // 下单之后的销量
        int afterSales = (Integer) goods.get("sales");
        // 订单数量
        long orderCnt = (Long) queryOneRow("select count(1) as orderCnt from `Order`").get("orderCnt");
        System.out.println("提交订单之前的库存：" + initStock);
        System.out.println("提交订单之前的销量：" + initSales);
        System.out.println("提交订单之后的库存：" + afterStock);
        System.out.println("提交订单之后的销量：" + afterSales);
        // 断言：提交订单之前的库存 - 购买商品的总数量 = 提交订单之后的库存
        assertEquals(initStock - submitCnt * goodsCount, afterStock);
        // 断言：销量 + 购买商品的总数量 = 提交订单之后的销量
        assertEquals(initSales + submitCnt * goodsCount, afterSales);
        // 断言：订单数量 = 调用提交订单接口的次数
        assertEquals(submitCnt, orderCnt);
    }

}
