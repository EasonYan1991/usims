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

//        List<Map> emptyData = sp.findList("select * from usims_data where title = '' ");
//
//
//        for(int i=1; i<emptyData.size(); i++){
//            String url = (String) emptyData.get(i).get("url");
//            String id =  (String) emptyData.get(i).get("id");
//            System.out.println("----" + url);
//            Document doc =  Jsoup.connect(url).get();
//            System.out.println(i + "/" + emptyData.size() + "----" + doc.title());
//            if("".equals(doc.title())){
//                    sp.update("delete from usims_data where id=?", new String[]{id});
//            }
//            else{
//                sp.update("update usims_data set title=? where id = ?" , new String[]{doc.title(), id });
//            }
//
//        }


        String content;

        String fileName;
//       emptyData = sp.findList("select * from usims_data order by sn");
        Map map ;
        String title;
        String id;
        int MAX;
        int sn;
//        MAX = emptyData.size();
        File dataDir = new File("data");
        File[] oldFile = dataDir.listFiles();
        String newFileName;
        String url;
        Document doc;
        for(int i=1;i<=oldFile.length; i++){

            File file = oldFile[i-1];
            if(!isNumber(file.getName())){
                continue;
            }
            sn = getSn(file.getName());
            map = sp.getBySn(sn);

            content = FileUtil.readFile(file);
            id = (String) map.get("id");

            url = (String)map.get("url");

            System.out.println(" ----> " + url);
            try {
                doc = Jsoup.connect(url).get();
                title = doc.title();
                newFileName = sn + "_" + title.replaceAll("/", "") + ".txt";

                System.out.println(i + "   "  +  file.getName() + " -> " + newFileName + " " + id + " " + url);
                assertTrue(file.getName() + " -> " + newFileName, file.renameTo(new File(dataDir, newFileName)));
                sp.update("update usims_data set filename=?, status=2, content=? where id=?", new String[]{newFileName, content, id});
            }
            catch (Exception ex){
                ex.printStackTrace();
            }


        }

    }

    boolean isNumber(String fileName){
        boolean is = fileName.contains(".md");
        return is;
    }

    int getSn(String fileName){
        return  Integer.valueOf(fileName.substring(0, fileName.indexOf(".md")));
    }
}
