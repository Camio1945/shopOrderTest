package com.erhu1999.shopordertest.version005;

import com.erhu1999.shopordertest.common.AbstractTest;
import com.erhu1999.shopordertest.common.Constant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.erhu1999.shopordertest.common.Config.DB_NAME_PREFIX;
import static com.erhu1999.shopordertest.common.Config.DB_PWD;
import static com.erhu1999.shopordertest.common.Config.DB_URL;
import static com.erhu1999.shopordertest.common.Config.DB_USER;
import static com.erhu1999.shopordertest.version005.JdbcUtil005.dropDbIfExistsThenCreateDb;
import static com.erhu1999.shopordertest.version005.JdbcUtil005.execSqlFile;
import static com.erhu1999.shopordertest.version005.JdbcUtil005.init;
import static com.erhu1999.shopordertest.version005.JdbcUtil005.queryOneRow;
import static com.erhu1999.shopordertest.version005.JdbcUtil005.renewUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 版本005的测试用例
 */
class Version005TestCommon extends AbstractTest {

    /** 数据库名称 */
    private static final String DB_NAME = DB_NAME_PREFIX + "version005";
    /** 数据库连接数量 */
    private static final int CONN_NUMBER = 4000;
    /** hikari数据源 */
    private static HikariDataSource hikariDataSource;

    static void initDb() throws SQLException {
        printSeparateLine(Version005TestCommon.class.getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        System.out.println("正在初始化数据库：" + DB_NAME);
        // 数据库数据库连接相关的参数
        init(DB_URL, DB_USER, DB_PWD);
        // 删除数据库（如果存在的话），然后创建数据库
        dropDbIfExistsThenCreateDb(DB_NAME);
        // 重新初始化数据库连接相关的参数
        renewUrl(DB_NAME);
        String sqlFilePath = Version005TestCommon.class.getResource("").getFile() + Version005TestCommon.class.getSimpleName() + ".sql";
        sqlFilePath = new File(sqlFilePath).getAbsolutePath().replaceAll("\\\\", "/");
        execSqlFile(sqlFilePath);
        System.out.println("初始化数据库完成：" + DB_NAME);
        // 初始化hikari的数据源
        initDataSourceByHikari();
    }

    /** 初始化hikari的数据源 */
    private static void initDataSourceByHikari() throws SQLException {
        String dbUrl = DB_URL.replace(Constant.DB_URL_PARAM, "/" + DB_NAME + Constant.DB_URL_PARAM);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(DB_USER);
        config.setPassword(DB_PWD);
        config.setMinimumIdle(CONN_NUMBER);
        config.setMaximumPoolSize(CONN_NUMBER * 2);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "" + CONN_NUMBER);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "" + (CONN_NUMBER * 2));
        hikariDataSource = new HikariDataSource(config);
        // 切换数据源
        JdbcUtil005.setDataSource(hikariDataSource);
        System.out.println("初始化hikari的数据源完成");
    }

    /**
     * 测试多线程
     *
     * @param threadCount 线程数
     * @throws Exception
     */
    static void testMultiThread(int threadCount) throws Exception {
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
        int submitCnt = 10000;
        if (threadCount < 1 || threadCount > submitCnt) {
            throw new RuntimeException("参数threadCount不正确");
        }
        // 记录每个线程应该提交几个订单
        Map<Integer, Integer> threadSubmitTimesMap = getThreadSubmitTimesMap(threadCount, submitCnt);
        // 开始时间（纳秒）
        long startTimeNanos = System.nanoTime();
        CountDownLatch startGate = new CountDownLatch(threadCount);
        CountDownLatch endGate = new CountDownLatch(submitCnt);
        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            Integer times = threadSubmitTimesMap.get(threadIndex);
            new Thread(() -> {
                try {
                    startGate.await();
                    for (int i = 0; i < times; i++) {
                        // 调用提交订单的接口
                        Version005.submitOrder(userId, goodsId, goodsCount, addrId);
                        endGate.countDown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            startGate.countDown();
        }
        endGate.await();
        // 结束时间（纳秒）
        long endTimeNanos = System.nanoTime();
        // 平均时间（纳秒）
        long avgTimeNanos = (endTimeNanos - startTimeNanos) / submitCnt;
        System.out.println("提交每个订单平均耗时的纳秒数：" + avgTimeNanos);
        System.out.println("每秒钟可以提交的订单数：" + 1000000000L / avgTimeNanos);
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

    /** 记录每个线程应该提交几个订单 */
    private static Map<Integer, Integer> getThreadSubmitTimesMap(int threadCount, int submitCnt) {
        Map<Integer, Integer> threadSubmitTimesMap = new LinkedHashMap<>();
        for (int i = 0; i < threadCount; i++) {
            threadSubmitTimesMap.put(i, 0);
        }
        for (int i = 0; i < submitCnt; i++) {
            int key = i % threadCount;
            int val = threadSubmitTimesMap.get(key);
            threadSubmitTimesMap.put(key, ++val);
        }
        return threadSubmitTimesMap;
    }

    /** 关闭数据源 */
    static void closeAllConnection() {
        hikariDataSource.close();
    }
}
