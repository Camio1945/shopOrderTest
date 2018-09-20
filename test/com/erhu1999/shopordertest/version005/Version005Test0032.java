package com.erhu1999.shopordertest.version005;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * 版本005的测试用例
 */
class Version005Test0032 extends AbstractTest {

    /** 在每个测试用例执行之前，先执行一遍初始化数据库的方法 */
    @BeforeEach
    void initDb() throws SQLException {
        Version005TestCommon.initDb();
    }

    @Test
    @DisplayName(Version005.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        Version005TestCommon.testMultiThread(32);
    }

    /** 在执行每个测试之后关闭数据源 */
    @AfterEach
    void closeAllConnection() {
        Version005TestCommon.closeAllConnection();
    }

}