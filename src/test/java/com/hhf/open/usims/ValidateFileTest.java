package com.hhf.open.usims;

import org.junit.Test;

import java.io.File;

/**
 * Created by Administrator on 2016/6/24.
 */
public class ValidateFileTest {

    @Test
    public void test(){
        File dataDir = new File("data");

        File[] fs = dataDir.listFiles();

        int i=1;
        for(File f : fs){
            if(!((i++)+".md").equals(f.getName())){
                System.out.println("---->" + f.getName());
            }
        }
    }
}
