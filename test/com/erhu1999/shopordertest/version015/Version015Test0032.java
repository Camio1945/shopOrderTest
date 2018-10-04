package com.erhu1999.shopordertest.version015;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.erhu1999.shopordertest.common.Constant.RANDOM;

/**
 * 版本015的测试用例
 */
class Version015Test0032 extends AbstractTest {

    @Test
    @DisplayName("第015版提交订单 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        for (int i = 0; i < 10; i++) {
            printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName() + " 第 " + (i + 1) + " 次测试");
            // 用于保证顺序是随机的
            if (RANDOM.nextInt(2) == 0) {
                testVersion015ParamAddr();
                testVersion015QueryAddr();
            } else {
                testVersion015QueryAddr();
                testVersion015ParamAddr();
            }
        }
        printSeparateLine("", "最终测试结果");
        Set<Class> clazzSet = Version015TestCommon.avgNanoTimeMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version015TestCommon.avgNanoTimeMap.get(clazz);
            double avgNanoTime = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 提交每个订单平均耗时的纳秒数： " + (long) avgNanoTime);
        }
        System.out.println();
        clazzSet = Version015TestCommon.avgSubmitTimesMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version015TestCommon.avgSubmitTimesMap.get(clazz);
            double avgSubmitTimes = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 每秒钟可以提交的订单数： " + (long) avgSubmitTimes);
        }

    }

    /** 测试分开提交 */
    private void testVersion015ParamAddr() throws Exception {
        printSeparateLine("", Version015ParamAddr.class.getSimpleName());
        Version015TestCommon.initDb();
        Version015TestCommon.testMultiThread(32, Version015ParamAddr.class);
        Version015TestCommon.closeAllConnection();
    }

    /** 测试一起提交 */
    private void testVersion015QueryAddr() throws Exception {
        printSeparateLine("", Version015QueryAddr.class.getSimpleName());
        Version015TestCommon.initDb();
        Version015TestCommon.testMultiThread(32, Version015QueryAddr.class);
        Version015TestCommon.closeAllConnection();
    }
}