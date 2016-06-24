package com.hhf.open.usims;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2016/6/23.
 */
public class Config {

    static {
        prop = new Properties();
        urls = new Properties();
        init();
    }
    private final static Properties prop;
    private final static Properties urls;

    public  static String[] getUrls(){

        String[] ret = new String[urls.size()];
        if(urls!=null) {

            Enumeration enums = urls.keys();
            int i=0;
            while (enums.hasMoreElements()) {
                Object key = enums.nextElement();
                ret[i++] = (String) urls.get(key);
            }

        }
        return ret;
    }

    /**
     * at windows C:/Users/Administrator/.usims/usims.properties file as
     * jdbc.driver=com.mysql.jdbc.Driver
     * jdbc.url=jdbc:mysql://xxx.xxx.xxx.xxx/usims_data?useUnicode=true&characterEncoding=utf-8&autoReconnect=true
     * jdbc.username=
     * jdbc.password=
     * urls.file=C:/Users/Administrator/.usims/urls.txt
     *
     * urls.txt
     *
     */
    private static void init(){
        File file = new File(System.getProperty("user.home") + "/.usims/usims.properties");
        try  {
            if(file.exists()){
                prop.load(new FileInputStream(file));
            }
            String urlsFile  = (String) prop.get("urls.file");
            File f = new File(urlsFile);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                line = line.trim();
                if(line.length()>0 && !line.startsWith("#")) {
                    urls.put(line, line);
                }
            }
        }

        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static String getJDBCDriver(){
        return (String) prop.get("jdbc.driver");
    }
    public static String getJDBCUrl(){
        return (String)prop.get("jdbc.url");
    }
    public static String getJDBCUsername(){
        return (String)prop.get("jdbc.username");
    }
    public static String getJDBCPassword(){
        return (String) prop.get("jdbc.password");
    }
}
