package com.erhu1999.shopordertest.version009;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本009的测试用例
 */
class Version009Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version009.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        printSeparateLine("", "同步范围为整个方法");
        Version009TestCommon.initDb();
        Version009TestCommon.testMultiThread(32, true);
        Version009TestCommon.closeAllConnection();

        printSeparateLine("", "同步范围为方法中的部分代码");
        Version009TestCommon.initDb();
        Version009TestCommon.testMultiThread(32, false);
        Version009TestCommon.closeAllConnection();
    }

}