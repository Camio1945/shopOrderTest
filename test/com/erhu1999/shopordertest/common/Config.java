package com.erhu1999.shopordertest.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.erhu1999.shopordertest.version001.JdbcUtil001.URL_PARAM;

/** 配置文件 */
public class Config {

    /** 数据库连接地址 */
    public static final String DB_URL;
    /** 数据库连接帐号 */
    public static final String DB_USER;
    /** 数据连接密码 */
    public static final String DB_PWD;
    /** 数据库名称前缀 */
    public static final String DB_NAME_PREFIX = "shop_order_test_";
    /** 配置文件名称 */
    private static final String PROPERTY_FILE_NAME = "config.properties";
    /** 数据库连接的URL 所对应的配置文件的key */
    private static final String DB_URL_PROPERTY_KEY = "dbUrl";
    /** 数据库连接的帐号 所对应的配置文件的key */
    private static final String DB_USER_PROPERTY_KEY = "dbUser";
    /** 数据库连接的密码 所对应的配置文件的key */
    private static final String DB_PWD_PROPERTY_KEY = "dbPwd";

    static {
        // 加载配置文件
        Properties properties = loadProperty();
        String dbUrl = properties.getProperty(DB_URL_PROPERTY_KEY);
        DB_URL = dbUrl + URL_PARAM;
        DB_USER = properties.getProperty(DB_USER_PROPERTY_KEY);
        DB_PWD = properties.getProperty(DB_PWD_PROPERTY_KEY);
        boolean isNull = dbUrl == null || DB_USER == null || DB_PWD == null;
        boolean isBlank = "".equals(dbUrl.trim()) || "".equals(DB_USER.trim()) || "".equals(DB_PWD.trim());
        if (isNull || isBlank) {
            throw new RuntimeException("请检查配置文件" + PROPERTY_FILE_NAME + "中的配置是否正确");
        }
    }

    /**
     * 加载配置文件
     *
     * @return
     */
    public static Properties loadProperty() {
        InputStream inputStream = null;
        try {
            inputStream = Config.class.getResourceAsStream("/" + PROPERTY_FILE_NAME);
            if (inputStream == null) {
                throw new RuntimeException("配置文件不存在：" + PROPERTY_FILE_NAME);
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载配置文件出错");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
