package com.erhu1999.shopordertest.common;

import java.util.Random;

/**
 * 常量
 *
 * @author HuKaiXuan
 */
public class Constant {
  /** 订单状态：待支付 */
  public static final int ORDER_STATUS_WAIT_PAY = 1;
  /** 数据库驱动类名 */
  public static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
  /** 数据库连接URL参数 */
  public static final String DB_URL_PARAM = "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2B8";
  /** 随机数 */
  public static final Random RANDOM = new Random();
  /** 1秒钟对应的纳秒数 */
  public static final long NANO_OF_ONE_SECOND = 1000000000L;
}
