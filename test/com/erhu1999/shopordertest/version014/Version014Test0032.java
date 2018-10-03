package com.erhu1999.shopordertest.version014;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.erhu1999.shopordertest.common.Constant.RANDOM;

/**
 * 版本014的测试用例
 */
class Version014Test0032 extends AbstractTest {

    @Test
    @DisplayName(Version014TogetherCommit.DISPLAY_NAME + " 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        for (int i = 0; i < 10; i++) {
            printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName() + " 第 " + (i + 1) + " 次测试");
            // 用于保证顺序是随机的
            if (RANDOM.nextInt(2) == 0) {
                testSeparateCommit();
                testTogetherCommit();
            } else {
                testTogetherCommit();
                testSeparateCommit();
            }
        }
        printSeparateLine("", "最终测试结果");
        Set<Class> clazzSet = Version014TestCommon.avgNanoTimeMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version014TestCommon.avgNanoTimeMap.get(clazz);
            double avgNanoTime = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 提交每个订单平均耗时的纳秒数： " + (long) avgNanoTime);
        }
        System.out.println();
        clazzSet = Version014TestCommon.avgSubmitTimesMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version014TestCommon.avgSubmitTimesMap.get(clazz);
            double avgSubmitTimes = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 每秒钟可以提交的订单数： " + (long) avgSubmitTimes);
        }

    }

    /** 测试分开提交 */
    private void testSeparateCommit() throws Exception {
        printSeparateLine("", Version014SeparateCommit.class.getSimpleName());
        Version014TestCommon.initDb();
        Version014TestCommon.testMultiThread(32, Version014SeparateCommit.class);
        Version014TestCommon.closeAllConnection();
    }

    /** 测试一起提交 */
    private void testTogetherCommit() throws Exception {
        printSeparateLine("", Version014TogetherCommit.class.getSimpleName());
        Version014TestCommon.initDb();
        Version014TestCommon.testMultiThread(32, Version014TogetherCommit.class);
        Version014TestCommon.closeAllConnection();
    }
}