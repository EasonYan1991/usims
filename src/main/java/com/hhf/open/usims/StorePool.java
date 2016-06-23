package com.hhf.open.usims;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhf.open.usims.MD5Util.encode;

/**
 * Created by Administrator on 2016/6/23.
 */
public class StorePool {
    Connection conn = null;
    String driver;



    public void loaded(String url){
        if(!isLoaded(url)) {
            String sql = "insert usims_loaded(id, url) values(?,?)";
            String id = encode(url);
            try {
                update(sql, new String[]{id, url});
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    public void setData(String url, String content){
        String sql = "insert usims_data(id, url, content) values(?,?,?)";
        String id = encode(url);
        try {
            update(sql, new String[]{id, url, content});
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoaded(String url){
        String id = encode(url);
        try {
            List list = findList("select count(1) as rs from usims_loaded where id ='"+ id+"'");
            return list !=null && list.size()>0 && (((Long)((Map)list.get(0)).get("rs"))>0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getDataCount(){

        try {
            List list = findList("select count(1) as rs from usims_data");
            if(list !=null && list.size()>0) {
                return ((Long) ((Map) list.get(0)).get("rs"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Connection getConnection(){
        return ConnectionManager.getSingletonConnect();
    }

    /**
     *
     * @return int
     **/
    private int update(String sql, String[] param) throws SQLException, ClassNotFoundException {
        int num = -1;
        Connection c = getConnection();
        PreparedStatement p = null;
        if (c != null)
        {
            try
            {
                p = c.prepareStatement(sql);
                if (param != null)
                {
                    for (int i = 0; i < param.length; i++)// 循环追加参数
                    {
                        p.setString(i + 1, param[i]);
                    }
                }
                num = p.executeUpdate(); // 执行更新命令，返回行数
                p.close();
                p = null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            closeDbAll(c, p, null);
        }
        return num;// 返回影响行数
    }

    /**
     *
     * @return void
     */
    private void closeDbAll(Connection c, PreparedStatement p, ResultSet r)
    {
        if (r != null)
        {
            try
            {
                r.close();
                r = null;
            }
            catch (SQLException e)
            {
                System.out.println("关闭r时发生错误：" + e.toString());
                ;
            }
        }
        if (p != null)
        {
            try
            {
                p.close();
                p = null;
            }
            catch (SQLException e)
            {
                System.out.println("关闭p时发生错误：" + e.toString());
                ;
            }
        }
        try
        {
            if (c != null)
            {
                if (!c.isClosed())
                {
                    c.close();
                }
                c = null;
            }
        }
        catch (Exception e)
        {
            System.out.println("关闭c是发生错误：" + e.toString());
        }
    }

    private List findList(String sql) throws SQLException{
        return findList(sql,null);
    }

    private List findList(String sql, Object[] values) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet result;
        List list=new ArrayList();
        try{

            preparedStatement = getConnection().prepareStatement(sql);
            setAttribute(preparedStatement, values);
            result= preparedStatement.executeQuery();
            ResultSetMetaData metadata=result.getMetaData();
            int mapSize=metadata.getColumnCount();
            while(result.next()){
                Map map=new HashMap(mapSize);
                for(int i=0;i<mapSize;i++){
                    map.put(metadata.getColumnLabel(i+1), result.getObject(metadata.getColumnLabel(i+1)));
                }
                list.add(map);
            }
        }catch(SQLException e){
            throw e;
        }
        return list;
    }

    /**
     *
     * @param preparedStatement
     * @param values
     * @throws SQLException
     */
    private void setAttribute(PreparedStatement preparedStatement,
                              Object[] values) throws SQLException {
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                System.out.println(values[i]);
                preparedStatement.setObject(i+1, values[i]);
            }
        }
    }
}
