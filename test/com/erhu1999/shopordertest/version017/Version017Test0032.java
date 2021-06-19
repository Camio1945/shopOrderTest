package com.erhu1999.shopordertest.version017;

import com.erhu1999.shopordertest.common.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.erhu1999.shopordertest.common.Constant.RANDOM;

/**
 * 版本017的测试用例
 */
class Version017Test0032 extends AbstractTest {

  @Test
  @DisplayName("第017版提交订单 多线程测试 线程数 0032")
  void testOf0032Threads() throws Exception {
    for (int i = 0; i < TEST_TIMES; i++) {
      printSeparateLine(this.getClass().getSimpleName(), new Exception().getStackTrace()[0].getMethodName() + " 第 " + (i + 1) + " 次测试");
      // 用于保证顺序是随机的
      int nextInt = RANDOM.nextInt(3);
      if (nextInt == 0) {
        testVersion017IntegerId();
        testVersion017LongId();
        testVersion017StringId();
      } else if (nextInt == 1) {
        testVersion017LongId();
        testVersion017StringId();
        testVersion017IntegerId();
      } else {
        testVersion017StringId();
        testVersion017IntegerId();
        testVersion017LongId();
      }
    }
    printSeparateLine("", "最终测试结果");
    Set<Class> clazzSet = Version017TestCommon.avgNanoTimeMap.keySet();
    for (Class clazz : clazzSet) {
      List<Long> list = Version017TestCommon.avgNanoTimeMap.get(clazz);
      double avgNanoTime = getTrimmedMean(list);
      System.out.println(clazz.getSimpleName() + " 提交每个订单平均耗时的纳秒数： " + (long) avgNanoTime);
    }
    System.out.println();
    clazzSet = Version017TestCommon.avgSubmitTimesMap.keySet();
    for (Class clazz : clazzSet) {
      List<Long> list = Version017TestCommon.avgSubmitTimesMap.get(clazz);
      double avgSubmitTimes = getTrimmedMean(list);
      System.out.println(clazz.getSimpleName() + " 每秒钟可以提交的订单数： " + (long) avgSubmitTimes);
    }
  }

  /** 测试Integer类型的ID */
  private void testVersion017IntegerId() throws Exception {
    printSeparateLine("", Version017IntegerId.class.getSimpleName());
    String sqlFileName = Version017IntegerId.class.getSimpleName() + ".sql";
    Version017TestCommon.initDb(sqlFileName);
    Version017TestCommon.testMultiThread(32, Version017IntegerId.class);
    Version017TestCommon.closeAllConnection();
  }

  /** 测试Long类型的ID */
  private void testVersion017LongId() throws Exception {
    printSeparateLine("", Version017LongId.class.getSimpleName());
    String sqlFileName = Version017LongId.class.getSimpleName() + ".sql";
    Version017TestCommon.initDb(sqlFileName);
    Version017TestCommon.testMultiThread(32, Version017LongId.class);
    Version017TestCommon.closeAllConnection();
  }

  /** 测试String类型的ID */
  private void testVersion017StringId() throws Exception {
    printSeparateLine("", Version017StringId.class.getSimpleName());
    String sqlFileName = Version017StringId.class.getSimpleName() + ".sql";
    Version017TestCommon.initDb(sqlFileName);
    Version017TestCommon.testMultiThread(32, Version017StringId.class);
    Version017TestCommon.closeAllConnection();
  }
}
