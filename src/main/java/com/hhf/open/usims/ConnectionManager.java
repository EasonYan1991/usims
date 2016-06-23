package com.hhf.open.usims;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/6/23.
 */
public class ConnectionManager {

    public static Connection getSingletonConnect() throws RuntimeException
    {
        Connection c = null;

        String driver = Config.getJDBCDriver();
        String url = Config.getJDBCUrl();
        String dbname = Config.getJDBCUsername();
        String dbpass = Config.getJDBCPassword();
        try
        {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            c = DriverManager.getConnection(url, dbname, dbpass);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        if (c == null)
        {
            System.out.println("lian jie kong !");
        }
        return c;
    }
}
