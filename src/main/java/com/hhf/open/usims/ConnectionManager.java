package com.hhf.open.usims;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class ConnectionManager {

    static List<Connection> conns = new LinkedList<Connection>();

    public static synchronized  Connection getConnection() throws RuntimeException, ClassNotFoundException, SQLException {

        if(conns.size()==0) {
            System.out.println("------------------------------ Create Connection ");
            String driver = Config.getJDBCDriver();
            String url = Config.getJDBCUrl();
            Class.forName(driver);
            return DriverManager.getConnection(url, Config.getJDBCUsername(), Config.getJDBCPassword());
        }
        return conns.remove(0);
    }

    public static synchronized void releaseConnection(Connection conn) throws SQLException {
        if (conn != null)
            {
                if (!conn.isClosed())
                {
//                    c.close();
                    conns.add(conn);
                }
//                c = null;
            }
    }
}
