package util;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Author: liuwei
 * Date: 2019-05-05 11:00
 * Desc: oracle数据源测试类
 * 在普通测试类中使用org.springframework.jdbc.core.JdbcTemplate操作数据库
 * 当需要连接oracle数据库时，JdbcTemplate使用以下方法获取：
 *     DataSource dataSource = new OracleDataSource();
 *     JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
 */
@Slf4j
public class OracleDataSource implements DataSource {

    private static final String dirverClassName = "oracle.jdbc.driver.OracleDriver";
    private static final String url = "jdbc:oracle:thin:@马赛克";
    private static final String user = "马赛克";
    private static final String pswd = "马赛克";

    //连接池
    private static LinkedList<Connection> pool = new LinkedList<Connection>();
    private static Connection instance = null;

    static {
        try {
            Class.forName(dirverClassName);
        } catch (ClassNotFoundException e) {
            log.error("找不到驱动类！", e);
        }
    }

    public OracleDataSource() {

    }

    /**
     * 获取数据源单例
     */
    public Connection getInstance() throws SQLException {
        synchronized (instance) {
            if (null == instance){
                instance = DriverManager.getConnection(url, user, pswd);
            }
        }
        return instance;
    }

    /**
     * 获取一个数据库连接
     */
    public Connection getConnection() throws SQLException {
        return getConnection(user, user);
    }

    /**
     * 连接归池
     */
    public void freeConnection(Connection conn) {
        pool.addLast(conn);
    }

    /**
     * 创建一个入池连接
     */
    private void createConnection(String username, String password) throws SQLException {
        pool.add(DriverManager.getConnection(url, user, pswd));
    }

    /**
     * 获取一个数据库连接
     */
    public Connection getConnection(String username, String password) throws SQLException {
        synchronized (pool) {
            if (pool.size() == 0) createConnection(username,password);
        }
        return pool.getFirst();
    }

    //其它实现的方法，持空即可
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
