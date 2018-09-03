package com.erhu1999.shopordertest.common;

import static com.erhu1999.shopordertest.common.JdbcUtil.URL_PARAM;

/** 配置文件 */
public class Config {
    /** 数据库连接地址，请不要在后面追加具体的数据库名称，数据库名称会在代码中追加。DB_URL是final类型的，需要在静态代码中组装。 */
    public static final String DB_URL;

    static {
        DB_URL = "jdbc:mysql://192.168.4.16:3306" + URL_PARAM;
    }

    /** 数据库连接帐号 */
    public static final String DB_USER = "root";
    /** 数据连接密码 */
    public static final String DB_PWD = "Dn-qFif4yrP&_up72";
    /** 数据库名称前缀 */
    public static final String DB_NAME_PREFIX = "shop_order_test_";

}
