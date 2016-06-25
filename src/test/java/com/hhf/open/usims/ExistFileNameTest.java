package com.hhf.open.usims;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.hhf.open.usims.UsimsAssert.assertNotEmpty;
import static com.hhf.open.usims.UsimsAssert.assertTrue;

/**
 * Created by Administrator on 2016/6/25.
 */
public class ExistFileNameTest {

    @Test
    public  void test() throws SQLException, ClassNotFoundException, IOException {

        StorePool sp = StorePool.getInst();

        List<Map> emptyData = sp.findList("select * from usims_data where title = '' ");


        for(int i=1; i<emptyData.size(); i++){
            String url = (String) emptyData.get(i).get("url");
            String id =  (String) emptyData.get(i).get("id");
            System.out.println("----" + url);
            Document doc =  Jsoup.connect(url).get();
            System.out.println(i + "/" + emptyData.size() + "----" + doc.title());
            if("".equals(doc.title())){
                    sp.update("delete from usims_data where id=?", new String[]{id});
            }
            else{
                sp.update("update usims_data set title=? where id = ?" , new String[]{doc.title(), id });
            }

        }


        String content;

        String fileName;
       emptyData = sp.findList("select * from usims_data where content is null");
        Map map ;
        String title;
        String id;
        int MAX;
        int sn;
        MAX = emptyData.size();
        for(int i=1;i<=emptyData.size(); i++){

            map = emptyData.get(i);

            fileName = (String) map.get("filename");
            title = (String)map.get("title");
            id =  (String) map.get("id");
            sn = (Integer)map.get("sn");
            assertNotEmpty("id is empty ---->" + i, id);

            File dataDir = new File("data");
            File file = new File(dataDir,  fileName + ".md");
            System.out.println("---->" + i + "/" + MAX  + " " + id);
            String newFileName;
            if(file.exists()){
                content = sp.getContentByFileName(fileName+".md");
                sp.update("update usims_data set content=? where id = ?" , new String[]{content, id });
                newFileName = sn + "_" + title + ".txt";
                File newFile = new File(dataDir, newFileName);
                assertTrue("rename " , file.renameTo(newFile));
            }

        }

    }
}
