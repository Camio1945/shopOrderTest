package com.erhu1999.shopordertest.version010;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本010的测试用例
 */
class Version010Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version010Method1.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());

        printSeparateLine("", "同步代码块多一些");
        Version010TestCommon.initDb();
        Version010TestCommon.testMultiThread(32, true);
        Version010TestCommon.closeAllConnection();

        printSeparateLine("", "同步代码块少一些");
        Version010TestCommon.initDb();
        Version010TestCommon.testMultiThread(32, false);
        Version010TestCommon.closeAllConnection();
    }
}