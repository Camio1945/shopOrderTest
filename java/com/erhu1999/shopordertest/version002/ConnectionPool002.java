package com.erhu1999.shopordertest.version002;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * 数据库连接池
 * 参考博客： https://www.cnblogs.com/xdp-gacl/p/4002804.html
 * 【备注】为了防止被其他测试包引用，这里没有把类设置为public类型，以防导致混乱
 *
 * @author HuKaiXuan
 */
class ConnectionPool002 implements DataSource {
    /** 使用LinkedList集合来存放数据库链接，由于要频繁读写List集合，所以这里使用LinkedList存储数据库连接比较合适 */
    private static LinkedList<Connection> connectionList = new LinkedList<>();

    /**
     * 初始化连接池
     *
     * @param connectionLinkedList 初始化连接列表
     */
    public static void initPool(LinkedList<Connection> connectionLinkedList) {
        if (connectionLinkedList == null || connectionLinkedList.size() == 0) {
            throw new RuntimeException("initList不能为空");
        }
        connectionList = connectionLinkedList;
    }

    /** 获取数据库连接 */
    @Override
    public Connection getConnection() {
        if (connectionList.size() <= 0) {
            throw new RuntimeException("数据库忙，无法获取数据库");
        }
        // 从connectionList集合中获取一个数据库连接
        final Connection conn = connectionList.removeFirst();
        // 返回Connection对象的代理对象
        return (Connection) Proxy.newProxyInstance(ConnectionPool002.class.getClassLoader(), conn.getClass().getInterfaces(), (proxy, method, args) -> {
            String close = "close";
            if (method.getName().equals(close)) {
                // 如果调用的是Connection对象的close方法，就把conn还给数据库连接池
                connectionList.add(conn);
                return null;
            }
            return method.invoke(conn, args);
        });
    }

    @Override
    public Connection getConnection(String username, String password) {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {

    }

    @Override
    public void setLoginTimeout(int seconds) {

    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }
}
