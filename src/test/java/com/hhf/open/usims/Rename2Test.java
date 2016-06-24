package com.hhf.open.usims;

import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.hhf.open.usims.MD5Util.encode;
import static com.hhf.open.usims.UsimsAssert.assertTrue;

/**
 * Created by Administrator on 2016/6/24.
 */
public class Rename2Test {

    @Test
    public  void test() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
        File dataDir = new File("data");
        File[] fs = dataDir.listFiles();

        StorePool sp = StorePool.getInst();

        List<Map> maxMap = sp.findList("select  MAX(CONVERT(filename,SIGNED)) maxId from  usims_data ");
        long maxId = (Long) maxMap.get(0).get("maxId");
        for(File f : fs){
            if(f.getName().contains(".aspx")){
                String url  = URLDecoder.decode(f.getName(), "UTF-8");

                String fileName  = String.valueOf(++maxId);


                String sql = "insert usims_data(id, filename, url) values(?,?,?)";
                String id = encode(url);
                sp.update(sql, new String[]{id, fileName, url});

                String newFileName = fileName + ".md";
                File newFile = new File(dataDir, newFileName);
                System.out.println(url +  " -> "  + fileName);
                boolean a = f.renameTo(newFile);
                assertTrue("rename fail", a);

            }
        }
    }
}
