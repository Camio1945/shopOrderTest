package com.erhu1999.shopordertest.version012;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本012的测试用例
 */
class Version012Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version012Normal.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());

        printSeparateLine("", "Version012Normal");
        Version012TestCommon.initDb();
        Version012TestCommon.testMultiThread(32, Version012Normal.class);
        Version012TestCommon.closeAllConnection();

        printSeparateLine("", "Version012FutureTask");
        Version012TestCommon.initDb();
        Version012TestCommon.testMultiThread(32, Version012FutureTask.class);
        Version012TestCommon.closeAllConnection();

    }
}