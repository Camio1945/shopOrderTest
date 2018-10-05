package com.erhu1999.shopordertest.common;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 抽象出一个测试的父类
 */
public abstract class AbstractTest {
    /** 测试次数 */
    public static final int TEST_TIMES = 12;

    /**
     * 打印
     *
     * @param objects
     */
    public static void p(Object... objects) {
        if (objects.length == 0) {
            System.out.println();
        } else {
            for (Object object : objects) {
                System.out.println(object);
            }
        }
    }

    /**
     * 打印分隔线
     *
     * @param classSingleName 类的基础名称
     * @param methodName      方法名称
     */
    public static void printSeparateLine(String classSingleName, String methodName) {
        p("\n------------------------------ 分隔线 " + classSingleName + " : " + methodName + " ------------------------------");
    }

    /**
     * 获取一个整型的并发的流
     *
     * @param size 列表大小
     * @return
     */
    public static Stream<Integer> parallelIntegerStream(int size) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        return Arrays.asList(new Integer[size]).parallelStream().map(i -> atomicInteger.incrementAndGet());
    }

    /** 获取去掉一个最大值、去掉一个最小值后的平均数（即去尾平均数） */
    public static double getTrimmedMean(List<Long> longList) {
        long min = longList.stream().mapToLong(Long::longValue).min().getAsLong();
        long max = longList.stream().mapToLong(Long::longValue).max().getAsLong();
        long[] removedMinAndMax = {-1, -1};
        double trimmedMean = longList.stream()
                .filter(l -> {
                    long longValue = l.longValue();
                    if (removedMinAndMax[0] == -1 && longValue == min) {
                        removedMinAndMax[0] = longValue;
                        return false;
                    }
                    if (removedMinAndMax[1] == -1 && longValue == max) {
                        removedMinAndMax[1] = longValue;
                        return false;
                    }
                    return true;
                })
                .mapToLong(Long::longValue)
                .average()
                .getAsDouble();
        System.out.println("去掉最小值：" + removedMinAndMax[0] + "  最大值：" + removedMinAndMax[1]);
        return trimmedMean;
    }
}
