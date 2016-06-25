package com.hhf.open.usims;

import java.io.*;
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

    String driver;


    static StorePool pool;
    private StorePool(){

    }

    public static StorePool getInst(){
        if(pool == null){
            pool =new StorePool();
        }
        return pool;
    }

    public void loaded(String title, String url) throws SQLException, ClassNotFoundException {
          String sql = "insert usims_loaded(id, title, url) values(?,?,?)";
          String id = encode(url);
          update(sql, new String[]{id, title, url});

    }




    public void setData(String title, String url, String content) throws SQLException, ClassNotFoundException {

        if(!existByUrl(url)) {
    //        String sql = "insert usims_data(id, url, content) values(?,?,?)";
    //        String id = encode(url);
    //        update(sql, new String[]{id, url, content});

            File dataDir = new File("data");
            if(!dataDir.exists() ){
                dataDir.mkdirs();
            }

            File file ;
            int maxId= 1;
            String fileName = maxId+".md";
            file = new File(dataDir, fileName);
            while(file.exists()){
                maxId++;
                fileName = maxId+".md";
                file = new File(dataDir, fileName);
            }


            writeToFile(file, content);

            String sql = "insert usims_data(id, title, filename, url) values(?,?,?,?)";
            String id = encode(url);
            update(sql, new String[]{id, title, maxId+"", url});
        }
    }

    public boolean existByFileName(String fileName) throws SQLException, ClassNotFoundException {
        List list = findList("select count(1) as rs from usims_data where filename ='"+ fileName+"'");
        return list !=null && list.size()>0 && (((Long)((Map)list.get(0)).get("rs"))>0);
    }

    public Map getByFileName(String fileName)throws SQLException, ClassNotFoundException {
        List<Map> list = findList("select * from usims_data where filename ='"+ fileName+"'");
        if( list !=null && list.size()>0)
        {
            return list.get(0);
        }
        return null;
    }

    public String getContentByFileName(String fileName) throws IOException {
        File dataDir = new File("data");
        File file = new File(dataDir, fileName);
        if (!file.exists()) {
            throw new RuntimeException("-----Not found file  " + fileName);
        }
        return  FileUtil.readFile(file);
    }


    public boolean isLoaded(String url) throws ClassNotFoundException, SQLException {
        String id = encode(url);
        List list = findList("select count(1) as rs from usims_loaded where id ='"+ id+"'");
        return list !=null && list.size()>0 && (((Long)((Map)list.get(0)).get("rs"))>0);
    }

    public boolean existByUrl(String url) throws SQLException, ClassNotFoundException {
        String id = encode(url);
        List  list = findList("select count(1) as rs from usims_data where id ='"+ id+"'");
        return  list !=null && list.size()>0 && (((Long)((Map)list.get(0)).get("rs"))>0);
    }

    public int getFileCount(){
        return  new File("data").listFiles().length;
    }

    public long getDataCount() throws ClassNotFoundException, SQLException {
            List list = findList("select count(1) as rs from usims_data");
            if(list !=null && list.size()>0) {
                return ((Long) ((Map) list.get(0)).get("rs"));
            }
        throw new RuntimeException("get data count error");
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        return ConnectionManager.getConnection();
    }

    /**
     *
     * @return int
     **/
    public int update(String sql, String[] param) throws SQLException, ClassNotFoundException {
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
            finally {
                closeDbAll(c, p, null);
            }

        }
        return num;// 返回影响行数
    }

    /**
     *
     * @return void
     */
    private void closeDbAll(Connection conn, PreparedStatement p, ResultSet r)
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
//            if (c != null)
//            {
//                if (!c.isClosed())
//                {
//                    c.close();
//                }
//                c = null;
//            }
            ConnectionManager.releaseConnection(conn);
        }
        catch (Exception e)
        {
            System.out.println("关闭c是发生错误：" + e.toString());
        }
    }

    public List findList(String sql) throws SQLException, ClassNotFoundException {
        return findList(sql,null);
    }

    private List findList(String sql, Object[] values) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = null;
        ResultSet result =null;
        List list=new ArrayList();
        Connection connection = null;
        try{
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
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
        finally {
            closeDbAll(connection, preparedStatement, result);
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

    private static void writeToFile(File file, String writerContent)  {

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(writerContent);
            writer.flush();
            writer.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
