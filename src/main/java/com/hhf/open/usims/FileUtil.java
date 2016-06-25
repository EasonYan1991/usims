package com.hhf.open.usims;

import java.io.*;

/**
 * Created by Administrator on 2016/6/25.
 */
public class FileUtil {

    public static void copy(Reader in, Writer out) throws IOException {
        int c = -1;
        while((c = in.read()) != -1) {
            out.write(c);
        }
    }

    public static String readFile(File file) throws IOException {
        Reader in = null;
        try {
            if (file != null && file.canRead()) {
                in =new FileReader(file);
                StringWriter out = new StringWriter();
                copy(in, out);
                return out.toString();
            }
        }
        finally {
            if(in!=null){
                in.close();
            }
        }
        return "";
    }

    public static void saveFile(File file,String content) throws IOException {
        Writer writer = null;
       try {
           writer = new FileWriter(file);
           writer.write(content);
           writer.close();
       }
       finally {
           if(writer!=null){
               writer.close();
           }
       }
    }
}
