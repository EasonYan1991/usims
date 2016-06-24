package com.hhf.open.usims;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.hhf.open.usims.UsimsAssert.assertTrue;

/**
 * Created by Administrator on 2016/6/24.
 */
public class RenameTest  {

    @Test
    public void test() throws SQLException, ClassNotFoundException, IOException {
        StorePool sp = StorePool.getInst();

        List<Map> data = sp.findList("select * from usims_data ");

        int i=1;
        String id;
        for(Map m : data ){
            String fileName  = String.valueOf(i++);
            id = (String) m.get("id");

            String oldFileName = URLEncoder.encode((String) m.get("url"), "utf-8");
            String newFileName = fileName +".md";
            File dataDir = new File("data");
            if(!dataDir.exists() ){
               throw new IOException(dataDir.getAbsolutePath());
            }

            File newFile  = new File(dataDir, newFileName);
            if(newFile.exists()){
                while(newFile.exists()){
                    fileName  = String.valueOf(i++);
                    newFileName  = fileName +".md";
                    newFile  = new File(dataDir, newFileName);
                }
            }

            File oldFile =  new File(dataDir, oldFileName);
            if(oldFile.exists()){
                System.out.println(oldFileName + " -> " + newFileName);
                boolean renamed = oldFile.renameTo(newFile);
                assertTrue("rename faile:" + oldFile.getAbsoluteFile(), renamed);
                sp.update("update  usims_data  set filename =? where id=?" , new String[]{fileName, id});
            }



        }


    }


}
