package com.hhf.open.usims;

import org.junit.Test;

import static com.hhf.open.usims.Config.getUrls;
import static com.hhf.open.usims.UsimsAssert.assertNotEmpty;

/**
 * Created by Administrator on 2016/6/23.
 */
public class LoaderTest {

//    @Test
    public static   void main(String[] args) throws Exception {


        LoaderCrawler crawler = new LoaderCrawler("crawl", true);
        crawler.setThreads(1);
        crawler.setTopN(100);
        //crawler.setResumable(true);

        /*start crawl with depth of 4*/
        crawler.start(4);

    }
}
