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
        init();
    }
    private final static Properties prop;
    public  static String[] getUrls(){
        String[] ret = new String[prop.size()];
        if(ret.length>0) {
            Enumeration enums = prop.keys();
            int i=0;
            while (enums.hasMoreElements()) {
                Object key = enums.nextElement();
                ret[i++] = (String) prop.get(key);
            }

        }
        return ret;
    }

    private static void init(){
        File file = new File(System.getProperty("user.home") + "/.usims/urls.txt");

//            if(file.exists()){
//                prop.load(new FileInputStream(file));
//            }
        try  {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                prop.put(line, line);
            }
        }

        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
