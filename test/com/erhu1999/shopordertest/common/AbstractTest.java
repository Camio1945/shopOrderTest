package com.erhu1999.shopordertest.common;

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

}
