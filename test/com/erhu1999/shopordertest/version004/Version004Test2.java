package com.erhu1999.shopordertest.version004;

import com.alibaba.druid.pool.DruidDataSource;
import com.erhu1999.shopordertest.common.AbstractTest;
import com.erhu1999.shopordertest.common.Constant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.erhu1999.shopordertest.common.Config.DB_NAME_PREFIX;
import static com.erhu1999.shopordertest.common.Config.DB_PWD;
import static com.erhu1999.shopordertest.common.Config.DB_URL;
import static com.erhu1999.shopordertest.common.Config.DB_USER;

/**
 * 版本004的测试用例2
 */
class Version004Test2 extends AbstractTest {
    /** 数据库名称 */
    private static final String DB_NAME = DB_NAME_PREFIX + "version004";
    /** 数据库连接数量 */
    private static final int CONN_NUMBER = 100;
    /** hikari的数据源 */
    private static HikariDataSource hikariDataSource;
    /** druid的数据源 */
    private static DruidDataSource druidDataSource;

    /** 在每个测试用例执行之前，先执行一遍初始化数据库的方法 */
    @BeforeEach
    void initDb() throws SQLException {
        printSeparateLine(Version004Test2.class.getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        // 初始化阿里巴巴druid的数据源
        initDataSourceByDruid();
        // 初始化hikari的数据源
        initDataSourceByHikari();
    }

    /** 初始化阿里巴巴druid的数据源 */
    private static void initDataSourceByDruid() throws SQLException {
        //设置连接参数
        druidDataSource = new DruidDataSource();
//        String dbUrl = DB_URL.replace(Constant.DB_URL_PARAM, "/" + DB_NAME + Constant.DB_URL_PARAM);
        druidDataSource.setUrl(DB_URL);
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
        System.out.println("初始化druid数据源完成");
    }

    /** 初始化hikari的数据源 */
    private static void initDataSourceByHikari() throws SQLException {
        String dbUrl = DB_URL.replace(Constant.DB_URL_PARAM, "/" + DB_NAME + Constant.DB_URL_PARAM);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PWD);
        config.setMinimumIdle(CONN_NUMBER);
        config.setMaximumPoolSize(CONN_NUMBER * 10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "" + CONN_NUMBER);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "" + (CONN_NUMBER * 10));
        hikariDataSource = new HikariDataSource(config);
        System.out.println("初始化hikari的数据源完成");
    }

    /** 获取连接后关闭连接 */
    private static boolean CLOSE_CONN_AFTER_GET = true;
    /** 获取连接后不关闭连接 */
    private static boolean NOT_CLOSE_CONN_AFTER_GET = false;

    @Test
    @DisplayName(Version004.DISPLAY_NAME + " hikari的数据源 与 druid 数据源的对比")
    void testDataSourceComprare() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        System.out.println("druid 获取一个数据库连接平均耗时：" + getAvgNanos(druidDataSource, NOT_CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后不关闭连接）");
        System.out.println("hikari 获取一个数据库连接平均耗时：" + getAvgNanos(hikariDataSource, NOT_CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后不关闭连接）");
        System.out.println("druid 获取一个数据库连接平均耗时：" + getAvgNanos(druidDataSource, NOT_CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后不关闭连接）");
        System.out.println("hikari 获取一个数据库连接平均耗时：" + getAvgNanos(hikariDataSource, NOT_CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后不关闭连接）");
        System.out.println();
        System.out.println("druid 获取一个数据库连接平均耗时：" + getAvgNanos(druidDataSource, CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后关闭连接）");
        System.out.println("hikari 获取一个数据库连接平均耗时：" + getAvgNanos(hikariDataSource, CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后关闭连接）");
        System.out.println("druid 获取一个数据库连接平均耗时：" + getAvgNanos(druidDataSource, CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后关闭连接）");
        System.out.println("hikari 获取一个数据库连接平均耗时：" + getAvgNanos(hikariDataSource, CLOSE_CONN_AFTER_GET) + " 纳秒（获取连接后关闭连接）");
    }

    /** 获取平均获取一个连接的纳秒数 */
    private long getAvgNanos(DataSource dataSource, boolean isCloseAfterGet) throws SQLException {
        long startTime = System.nanoTime();
        List<Connection> connectionList = new ArrayList<>();

        for (int i = 0; i < CONN_NUMBER; i++) {
            Connection connection = dataSource.getConnection();
            if (isCloseAfterGet) {
                connection.close();
            } else {
                connectionList.add(connection);
            }
        }
        long endTime = System.nanoTime();
        for (Connection connection : connectionList) {
            connection.close();
        }
        return (endTime - startTime) / CONN_NUMBER;
    }

}
