package com.erhu1999.shopordertest.version017;

import com.erhu1999.shopordertest.common.Constant;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.erhu1999.shopordertest.common.AssertUtil.assertBasicStr;
import static com.erhu1999.shopordertest.common.AssertUtil.assertFileExists;

/**
 * JDBC工具类
 * 【备注】为了防止被其他测试包引用，这里没有把类设置为public类型，以防导致混乱
 *
 * @author HuKaiXuan
 */
class JdbcUtil017 {
  /** 数据库驱动 */
  private static String driver = Constant.DRIVER_CLASS_NAME;
  /** 数据库URL */
  private static String url = "";
  /** 数据库帐号 */
  private static String user = "";
  /** 数据库密码 */
  private static String password = "";

  // 加载驱动、放在静态代码块中，保证驱动在整个项目中只加载一次，提高效率
  static {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * 更新URL，更新之前的URL连接是不带数据库名称的，更新之后的是带数据库名称的
   *
   * @param dbName 数据库名称，如：shop_order_test_version001
   */
  public static void renewUrl(String dbName) {
    assertBasicStr(dbName);
    url = url.replace(Constant.DB_URL_PARAM, "/" + dbName + Constant.DB_URL_PARAM);
  }

  /** 初始化 */
  public static void init(String jdbcUrl, String jdbcUser, String jdbcPassword) {
    url = jdbcUrl;
    user = jdbcUser;
    password = jdbcPassword;
  }

  /** 数据库类型-ORACLE */
  public static final int DB_TYPE_ORACLE = 1;
  /** 数据库类型-MYSQL */
  public static final int DB_TYPE_MYSQL = 2;
  /** 数据库类型-SQLSERVER */
  public static final int DB_TYPE_SQLSERVER = 3;

  /**
   * 新建数据库连接
   *
   * @return java.sql.Connection 对象
   */
  public static Connection newConnection() {
    try {
      return DriverManager.getConnection(url, user, password);
    } catch (Exception e) {
      System.out.println("获取数据库连接失败，请检查配置文件是否正确：config.properties");
      e.printStackTrace();
      return null;
    }
  }

  /** 数据源 */
  private static DataSource dataSource = null;

  /** 设置数据库源 */
  public static void setDataSource(DataSource dataSourceOut) {
    dataSource = dataSourceOut;
  }

  /**
   * 获取数据库连接
   *
   * @return java.sql.Connection 对象
   */
  public static Connection getConnectionFromPool() throws SQLException {
    return dataSource.getConnection();
  }

  /**
   * 用于执行更新的方法,包括（insert delete update）操作
   *
   * @param sql String 类型的SQL语句
   * @return Integer 表示受影响的行数
   */
  public static int update(String sql) {
    // 定义变量用来判断更新操作是否成功，如果返回-1说明没有影响到更新操作的数据库记录条数，即更新操作失败
    int row = -1;
    Connection conn = null;
    Statement st = null;
    try {
      // 如果数据库链接被关闭了，就要既得一个新的链接
      conn = getConnectionFromPool();
      // 使用Connection对象conn的createStatement()创建Statement（数据库语句对象）st
      st = conn.createStatement();
      // 执行更新操作，返回影响的记录条数row
      row = st.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      close(conn, st, null, null);
    }
    return row;
  }

  /**
   * 删除数据库（如果存在的话）
   *
   * @param dbName 数据库名称
   */
  public static void dropDbIfExists(String dbName) {
    assertBasicStr(dbName);
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = newConnection();
      stmt = conn.createStatement();
      String sql = "drop database if exists " + dbName;
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      close(conn, stmt, null, null);
    }
  }

  /**
   * 创建数据库
   *
   * @param dbName 数据库名称
   */
  public static void createDb(String dbName) {
    assertBasicStr(dbName);
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = newConnection();
      stmt = conn.createStatement();
      String sql = "create database " + dbName + " default character set utf8mb4 collate utf8mb4_bin";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      close(conn, stmt, null, null);
    }
  }

  /**
   * 删除数据库（如果存在的话），然后创建数据库
   *
   * @param dbName 数据库名称
   */
  public static void dropDbIfExistsThenCreateDb(String dbName) {
    dropDbIfExists(dbName);
    createDb(dbName);
  }

  /**
   * 查询一条记录，要依赖于下面的queryToList方法，注意泛型的使用
   *
   * @param sql
   * @return　Map<String,Object>
   */
  public static Map<String, Object> queryOneRow(String sql) {
    // 执行下面的queryToList方法
    List<Map<String, Object>> list = queryToList(sql);
    // 三目运算，查询结果list不为空返回list中第一个对象,否则返回null
    return list.size() > 0 ? list.get(0) : null;
  }

  /**
   * 返回查询结果列表，形如：[{TEST_NAME=aaa, TEST_NO=2, TEST_PWD=aaa}, {TEST_NAME=bbb, TEST_NO=3, TEST_PWD=bbb}...]
   *
   * @param sql
   */
  public static List<Map<String, Object>> queryToList(String sql) {
    // 创建集合列表用以保存所有查询到的记录
    List<Map<String, Object>> list = new LinkedList<>();
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      conn = getConnectionFromPool();
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      // ResultSetMetaData 是结果集元数据，可获取关于 ResultSet 对象中列的类型和属性信息的对象 例如：结果集中共包括多少列，每列的名称和类型等信息
      ResultSetMetaData rsmd = rs.getMetaData();
      // 获取结果集中的列数
      int columncount = rsmd.getColumnCount();
      // while条件成立表明结果集中存在数据
      addIntoList(list, rsmd, columncount, rs);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      close(conn, st, null, rs);
    }
    return list;
  }

  /** 加入列表 */
  private static void addIntoList(List<Map<String, Object>> list, ResultSetMetaData rsmd, int columncount, ResultSet rs) throws SQLException {
    while (rs.next()) {
      // 创建一个HashMap用于存储一条数据
      HashMap<String, Object> onerow = new HashMap<>(columncount);
      // 循环获取结果集中的列名及列名所对应的值，每次循环都得到一个对象，形如：{TEST_NAME=aaa, TEST_NO=2, TEST_PWD=aaa}
      for (int i = 0; i < columncount; i++) {
        // 获取指定列的名称，注意orcle中列名的大小写
        String columnName = rsmd.getColumnName(i + 1);
        onerow.put(columnName, rs.getObject(i + 1));
      }
      // 将获取到的对象onewrow={TEST_NAME=aaa, TEST_NO=2, TEST_PWD=aaa}放到集合列表中
      list.add(onerow);
    }
  }

  /** 关闭数据库各种资源Connection Statement PreparedStatement ResultSet的方法 */
  public static void close(Connection conn, Statement st, PreparedStatement ps, ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    if (st != null) {
      try {
        st.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    if (ps != null) {
      try {
        ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    try {
      if (conn != null && !conn.isClosed()) {
        try {
          conn.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * 执行Sql文件
   *
   * @param sqlFilePath sql文件路径
   */
  public static void execSqlFile(String sqlFilePath) {
    assertFileExists(sqlFilePath);
    Connection conn = null;
    Statement stmt = null;
    try {
      String sqlFileContent = processSqlContent(sqlFilePath);
      String[] sqlArr = sqlFileContent.split(";\n");
      conn = newConnection();
      stmt = conn.createStatement();
      for (String singleSql : sqlArr) {
        stmt.addBatch(singleSql);
      }
      stmt.executeBatch();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      close(conn, stmt, null, null);
    }
  }

  /** --------------------------------- 以下是私有方法 --------------------------------- */

  /**
   * 处理Sql内容，把注释删除
   * 参考： http://53873039oycg.iteye.com/blog/2018780
   *
   * @param sqlFilePath sql文件路径
   * @return
   * @throws Exception
   */
  private static String processSqlContent(String sqlFilePath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(sqlFilePath), StandardCharsets.UTF_8);
    int capacity = 5120;
    StringBuffer contentBuffer = new StringBuffer(capacity);
    String tmpResult;
    boolean isStart = false;
    for (String temp : lines) {
      // 去空格
      tmpResult = new String(temp.replaceAll("\\s{2,}", " "));
      if (tmpResult != null) {
        // 去除同一行/* */注释
        if (tmpResult.indexOf("/*") != -1 && tmpResult.indexOf("*/") != -1) {
          // 最小匹配
          tmpResult = tmpResult.replaceAll("\\/\\*.*?\\*\\/", "");
        } else if (tmpResult.indexOf("/*") != -1 && tmpResult.indexOf("*/") == -1 && tmpResult.indexOf("--") == -1) {
          // /*开始
          isStart = true;
        } else if (tmpResult.indexOf("/*") != -1 && tmpResult.indexOf("--") != -1 && tmpResult.indexOf("--") < tmpResult.indexOf("/*")) {
          // 同时存在--/*
          tmpResult = tmpResult.replaceAll("--.*", "");
        }
        if (isStart && tmpResult.indexOf("*/") != -1) {
          // */结束
          isStart = false;
          continue;
        }
        // 去除同一行的--注释
        tmpResult = new String(tmpResult.replaceAll("--.*", ""));
      }
      if (!isStart) {
        // 保留换行符
        contentBuffer.append(tmpResult).append("\r\n");
      }
    }
    String content = contentBuffer.toString().trim().replaceAll("\r", "");
    String doubleBreak = "\n\n";
    while (content.contains(doubleBreak)) {
      content = content.replaceAll(doubleBreak, "\n");
    }
    return content;
  }

}


