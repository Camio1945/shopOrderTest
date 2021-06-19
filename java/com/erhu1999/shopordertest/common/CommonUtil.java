package com.erhu1999.shopordertest.common;

/**
 * 通用的工具类
 *
 * @author HuKaiXuan
 */
public class CommonUtil {
  /**
   * 字符串是否为空串
   *
   * @param str 字符串
   * @return
   */
  public static boolean isBlank(String str) {
    return str == null || "".equals(str.trim());
  }
}
