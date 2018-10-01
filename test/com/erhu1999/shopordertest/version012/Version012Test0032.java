package com.erhu1999.shopordertest.version012;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.erhu1999.shopordertest.common.Constant.RANDOM;

/**
 * 版本012的测试用例
 */
class Version012Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version012Normal.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        for (int i = 0; i < 10; i++) {
            printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName() + " 第 " + (i + 1) + " 次测试");
            // 用于保证顺序是随机的
            if (RANDOM.nextInt(2) == 0) {
                testNormal();
                testFutureTask();
            } else {
                testFutureTask();
                testNormal();
            }
        }
        printSeparateLine("", "最终测试结果");
        Set<Class> clazzSet = Version012TestCommon.avgNanoTimeMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version012TestCommon.avgNanoTimeMap.get(clazz);
            double avgNanoTime = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 提交每个订单平均耗时的纳秒数： " + (long) avgNanoTime);
        }
        System.out.println();
        clazzSet = Version012TestCommon.avgSubmitTimesMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version012TestCommon.avgSubmitTimesMap.get(clazz);
            double avgSubmitTimes = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 每秒钟可以提交的订单数： " + (long) avgSubmitTimes);
        }

    }

    /** 测试普通版本的提交订单 */
    private void testNormal() throws Exception {
        printSeparateLine("", "Version012Normal");
        Version012TestCommon.initDb();
        Version012TestCommon.testMultiThread(32, Version012Normal.class);
        Version012TestCommon.closeAllConnection();
    }

    /** 测试FutureTask版本的提交订单 */
    private void testFutureTask() throws Exception {
        printSeparateLine("", "Version012FutureTask");
        Version012TestCommon.initDb();
        Version012TestCommon.testMultiThread(32, Version012FutureTask.class);
        Version012TestCommon.closeAllConnection();
    }
}