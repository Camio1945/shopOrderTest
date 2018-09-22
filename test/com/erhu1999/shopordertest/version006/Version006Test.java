package com.erhu1999.shopordertest.version006;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 版本006的测试用例
 */
class Version006Test extends AbstractTest {
    /** 线程数量数组 */
    private static int[] threadCountArr = {32, 48, 72, 108, 162, 243, 365, 548, 822, 1233};

    @Test
    @DisplayName(Version006.DISPLAY_NAME + " 多线程测试")
    void testOf0000Threads() throws Exception {
        for (int threadCount : threadCountArr) {
            printSeparateLine(this.getClass().getSimpleName(), "并发线程数量 " + threadCount);
            Version006TestCommon.initDb();
            Version006TestCommon.testMultiThread(threadCount);
            Version006TestCommon.closeAllConnection();
        }
    }

}