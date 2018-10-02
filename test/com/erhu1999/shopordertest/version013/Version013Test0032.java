package com.erhu1999.shopordertest.version013;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.erhu1999.shopordertest.common.Constant.RANDOM;

/**
 * 版本013的测试用例
 */
class Version013Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version013LessColumn.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        for (int i = 0; i < 10; i++) {
            printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName() + " 第 " + (i + 1) + " 次测试");
            // 用于保证顺序是随机的
            if (RANDOM.nextInt(2) == 0) {
                testLessColumn();
                testFullColumn();
            } else {
                testFullColumn();
                testLessColumn();
            }
        }
        printSeparateLine("", "最终测试结果");
        Set<Class> clazzSet = Version013TestCommon.avgNanoTimeMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version013TestCommon.avgNanoTimeMap.get(clazz);
            double avgNanoTime = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 提交每个订单平均耗时的纳秒数： " + (long) avgNanoTime);
        }
        System.out.println();
        clazzSet = Version013TestCommon.avgSubmitTimesMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version013TestCommon.avgSubmitTimesMap.get(clazz);
            double avgSubmitTimes = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 每秒钟可以提交的订单数： " + (long) avgSubmitTimes);
        }

    }

    /** 测试查询时查询少一点字段（共少了4个字段） */
    private void testLessColumn() throws Exception {
        printSeparateLine("", Version013LessColumn.class.getSimpleName());
        Version013TestCommon.initDb();
        Version013TestCommon.testMultiThread(32, Version013LessColumn.class);
        Version013TestCommon.closeAllConnection();
    }

    /** 测试查询时查询全字段 */
    private void testFullColumn() throws Exception {
        printSeparateLine("", Version013FullColumn.class.getSimpleName());
        Version013TestCommon.initDb();
        Version013TestCommon.testMultiThread(32, Version013FullColumn.class);
        Version013TestCommon.closeAllConnection();
    }
}