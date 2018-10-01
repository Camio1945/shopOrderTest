package com.erhu1999.shopordertest.version011;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本011的测试用例
 */
class Version011Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version011Good.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());

        printSeparateLine("", "性能差、结果正确的版本");
        Version011TestCommon.initDb();
        Version011TestCommon.testMultiThread(32, Version011Bad.class);
        Version011TestCommon.closeAllConnection();

        printSeparateLine("", "性能好、结果正确的版本");
        Version011TestCommon.initDb();
        Version011TestCommon.testMultiThread(32, Version011Good.class);
        Version011TestCommon.closeAllConnection();

        printSeparateLine("", "性能好、结果错误的版本");
        Version011TestCommon.initDb();
        Version011TestCommon.testMultiThread(32, Version011Wrong.class);
        Version011TestCommon.closeAllConnection();
    }
}