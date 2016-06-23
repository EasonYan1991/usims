package com.hhf.open.usims;

import static com.hhf.open.usims.Config.getUrls;
import static com.hhf.open.usims.UsimsAssert.assertNotEmpty;

/**
 * at windows C:/Users/Administrator/.usims/usims.properties file as
 * jdbc.driver=com.mysql.jdbc.Driver
 * jdbc.url=jdbc:mysql://xxx.xxx.xxx.xxx/usims_data?useUnicode=true&characterEncoding=utf-8&autoReconnect=true
 * jdbc.username=
 * jdbc.password=
 * domain.urls=http://www.yourdomain.com/contract/
 *
 * build a table in the mysql database
 *  run sql script:
 *
 *
 DROP TABLE IF EXISTS `usims_data`;
 CREATE TABLE `usims_data` (
 `id` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
 `url` varchar(255) COLLATE utf8_bin DEFAULT NULL,
 `status` varchar(1) COLLATE utf8_bin DEFAULT NULL,
 `content` text COLLATE utf8_bin,
 PRIMARY KEY (`id`),
 UNIQUE KEY `url_index` (`url`) USING HASH
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {

        String[] urls = getUrls();
        assertNotEmpty("Get Urls", urls);


        LoaderCrawler crawler = new LoaderCrawler(urls, "crawl", true);
        crawler.setThreads(1);
        crawler.setTopN(100);
        //crawler.setResumable(true);
        /*start crawl with depth of 4*/
        crawler.start(4);
    }
}
