package com.erhu1999.shopordertest.common;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 抽象出一个测试的父类
 */
public abstract class AbstractTest {
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
}
