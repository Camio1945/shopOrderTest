package com.erhu1999.shopordertest.common;

import java.io.File;
import java.util.regex.Pattern;

import static com.erhu1999.shopordertest.common.CommonUtil.isBlank;

/**
 * 用于检查参数
 *
 * @author HuKaiXuan
 */
public class AssertUtil {
  /**
   * 字符串不允许为空
   *
   * @param str 字符串
   */
  public static void assertNotBlank(String str) {
    if (isBlank(str)) {
      throw new RuntimeException("字符串不允许为空");
    }
  }

  /**
   * 字符串不允许为空
   *
   * @param str 字符串
   * @param msg 消息
   */
  public static void assertNotBlank(String str, String msg) {
    if (isBlank(str)) {
      throw new RuntimeException(msg);
    }
  }

  /** 正则：字母、数量、下划线 */
  private static final Pattern BASIC_STR_PATTERN = Pattern.compile("^[0-9a-zA-Z_]+$");

  /**
   * 字符串必须是字母、数量、下划线的组合
   *
   * @param str 字符串
   */
  public static void assertBasicStr(String str) {
    if (isBlank(str) || !BASIC_STR_PATTERN.matcher(str).matches()) {
      throw new RuntimeException("字符串必须是字母、数量、下划线的组合");
    }
  }

  /**
   * 字符串必须是字母、数量、下划线的组合
   *
   * @param str 字符串
   * @param msg 消息
   */
  public static void assertBasicStr(String str, String msg) {
    if (isBlank(str) || !BASIC_STR_PATTERN.matcher(str).matches()) {
      throw new RuntimeException(msg);
    }
  }

  /**
   * 文件必须存在
   *
   * @param filePath 文件路径
   */
  public static void assertFileExists(String filePath) {
    if (isBlank(filePath) || !new File(filePath).exists()) {
      throw new RuntimeException("文件路径不存在：" + filePath);
    }
  }

  /**
   * 对象不能为空
   *
   * @param obj 对象
   */
  public static void assertNotNull(Object obj) {
    if (obj == null) {
      throw new RuntimeException("对象不能为空");
    }
  }

  /**
   * 对象不能为空
   *
   * @param obj 对象
   * @param msg 消息
   */
  public static void assertNotNull(Object obj, String msg) {
    if (obj == null) {
      throw new RuntimeException(msg);
    }
  }

  /**
   * 断言bool为真（即如果bool为假，则抛出异常）
   *
   * @param bool 布尔类型
   * @param msg  消息
   */
  public static void assertBoolIsTrue(boolean bool, String msg) {
    if (!bool) {
      throw new RuntimeException(msg);
    }
  }

}
