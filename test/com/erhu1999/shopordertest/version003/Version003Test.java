package com.erhu1999.shopordertest.version003;

import com.alibaba.druid.pool.DruidDataSource;
import com.erhu1999.shopordertest.common.AbstractTest;
import com.erhu1999.shopordertest.common.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

import static com.erhu1999.shopordertest.common.Config.DB_NAME_PREFIX;
import static com.erhu1999.shopordertest.common.Config.DB_PWD;
import static com.erhu1999.shopordertest.common.Config.DB_URL;
import static com.erhu1999.shopordertest.common.Config.DB_USER;
import static com.erhu1999.shopordertest.version003.JdbcUtil003.dropDbIfExistsThenCreateDb;
import static com.erhu1999.shopordertest.version003.JdbcUtil003.execSqlFile;
import static com.erhu1999.shopordertest.version003.JdbcUtil003.init;
import static com.erhu1999.shopordertest.version003.JdbcUtil003.queryOneRow;
import static com.erhu1999.shopordertest.version003.JdbcUtil003.renewUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 版本003的测试用例
 */
class Version003Test extends AbstractTest {
    /** 数据库名称 */
    private static final String DB_NAME = DB_NAME_PREFIX + "version003";
    /** 数据库连接数量 */
    private static final int CONN_NUMBER = 100;

    /** 在每个测试用例执行之前，先执行一遍初始化数据库的方法 */
    @BeforeEach
    void initDb() throws SQLException {
        printSeparateLine(Version003Test.class.getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        System.out.println("正在初始化数据库：" + DB_NAME);
        // 数据库数据库连接相关的参数
        init(DB_URL, DB_USER, DB_PWD);
        // 删除数据库（如果存在的话），然后创建数据库
        dropDbIfExistsThenCreateDb(DB_NAME);
        // 重新初始化数据库连接相关的参数
        renewUrl(DB_NAME);
        String sqlFilePath = Version003Test.class.getResource("").getFile() + Version003Test.class.getSimpleName() + ".sql";
        sqlFilePath = new File(sqlFilePath).getAbsolutePath().replaceAll("\\\\", "/");
        execSqlFile(sqlFilePath);
        System.out.println("初始化数据库完成：" + DB_NAME);
        // 初始化阿里巴巴druid的数据源
        initDataSourceByDruid();
    }

    /** 初始化阿里巴巴druid的数据源 */
    private static void initDataSourceByDruid() throws SQLException {
        //设置连接参数
        DruidDataSource druidDataSource = new DruidDataSource();
        String dbUrl = DB_URL.replace(Constant.DB_URL_PARAM, "/" + DB_NAME + Constant.DB_URL_PARAM);
        druidDataSource.setUrl(dbUrl);
        druidDataSource.setDriverClassName(Constant.DRIVER_CLASS_NAME);
        druidDataSource.setUsername(DB_USER);
        druidDataSource.setPassword(DB_PWD);
        //配置初始化大小、最小、最大
        druidDataSource.setInitialSize(CONN_NUMBER);
        druidDataSource.setMinIdle(CONN_NUMBER);
        druidDataSource.setMaxActive(CONN_NUMBER * 2);
        //连接泄漏监测
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(30);
        //配置获取连接等待超时的时间
        druidDataSource.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        // 初始化
        druidDataSource.init();
        // 切换数据源
        JdbcUtil003.setDataSource(druidDataSource);
        System.out.println("初始化druid数据源完成");
    }

    @Test
    @DisplayName(Version003.DISPLAY_NAME + " 单线程测试 阿里巴巴druid的数据源")
    void testDataSourceByDruid() throws Exception {
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
            Version003.submitOrder(userId, goodsId, goodsCount, addrId);
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
