package com.erhu1999.shopordertest.version016;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.erhu1999.shopordertest.common.Constant.RANDOM;

/**
 * 版本016的测试用例
 */
class Version016Test0032 extends AbstractTest {

    @Test
    @DisplayName("第016版提交订单 多线程测试 线程数 0032")
    void testOf0032Threads() throws Exception {
        for (int i = 0; i < 10; i++) {
            printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName() + " 第 " + (i + 1) + " 次测试");
            // 用于保证顺序是随机的
            if (RANDOM.nextInt(2) == 0) {
                testVersion016ParamAddr();
                testVersion016WithCache();
            } else {
                testVersion016WithCache();
                testVersion016ParamAddr();
            }
        }
        printSeparateLine("", "最终测试结果");
        Set<Class> clazzSet = Version016TestCommon.avgNanoTimeMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version016TestCommon.avgNanoTimeMap.get(clazz);
            double avgNanoTime = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 提交每个订单平均耗时的纳秒数： " + (long) avgNanoTime);
        }
        System.out.println();
        clazzSet = Version016TestCommon.avgSubmitTimesMap.keySet();
        for (Class clazz : clazzSet) {
            List<Long> list = Version016TestCommon.avgSubmitTimesMap.get(clazz);
            double avgSubmitTimes = list.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .getAsDouble();
            System.out.println(clazz.getSimpleName() + " 每秒钟可以提交的订单数： " + (long) avgSubmitTimes);
        }

    }

    /** 测试商品没有缓存 */
    private void testVersion016ParamAddr() throws Exception {
        printSeparateLine("", Version016NoCache.class.getSimpleName());
        Version016TestCommon.initDb();
        Version016TestCommon.testMultiThread(32, Version016NoCache.class);
        Version016TestCommon.closeAllConnection();
    }

    /** 测试商品有缓存 */
    private void testVersion016WithCache() throws Exception {
        printSeparateLine("", Version016WithCache.class.getSimpleName());
        Version016TestCommon.initDb();
        Version016TestCommon.testMultiThread(32, Version016WithCache.class);
        Version016TestCommon.closeAllConnection();
    }
}