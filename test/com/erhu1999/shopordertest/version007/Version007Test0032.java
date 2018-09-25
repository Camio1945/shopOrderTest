package com.erhu1999.shopordertest.version007;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本007的测试用例
 */
class Version007Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version007.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName());
        printSeparateLine("", "方法未同步，得到了错误的库存：-1");
        Version007TestCommon.initDb();
        Version007TestCommon.testMultiThread(32, false);
        Version007TestCommon.closeAllConnection();

        printSeparateLine("", "方法同步了，得到了正确的库存：0");
        Version007TestCommon.initDb();
        Version007TestCommon.testMultiThread(32, true);
        Version007TestCommon.closeAllConnection();
    }

}