package com.hhf.open.usims;

/**
 * Created by Administrator on 2016/6/23.
 */
public class UsimsAssert {


    public static void fail(String msg){
        throw  new RuntimeException(msg);
    };

    public static void assertTrue(String msg, boolean vals){
        if(!vals){
            fail(msg);
        }
    };

    public static void assertArrayNotEmpty(String msg, String[] vals){
        assertTrue(msg, vals!=null && vals.length>0);
    }

    public static void assertNotEmpty(String msg, String val){
        assertTrue(msg, val!=null && val.length()>0);
    }

    public static void assertNotNull(String msg, Object val){
        assertTrue(msg, val!=null);
    }
}
