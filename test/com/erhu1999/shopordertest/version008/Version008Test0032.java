package com.erhu1999.shopordertest.version008;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本008的测试用例
 */
class Version008Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version008Normal.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        printSeparateLine("", "不是static方法");
        Version008TestCommon.initDb();
        Version008TestCommon.testMultiThread(32, false);
        Version008TestCommon.closeAllConnection();

        printSeparateLine("", "是static方法");
        Version008TestCommon.initDb();
        Version008TestCommon.testMultiThread(32, true);
        Version008TestCommon.closeAllConnection();
    }

}