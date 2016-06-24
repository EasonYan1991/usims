package com.hhf.open.usims;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/6/23.
 */
public class LoaderCrawler extends BreadthCrawler {

    StorePool store;
    String[] orginUrls;
   static Set<String> urlsSet = new HashSet<String>();

    public LoaderCrawler(String crawlPath, boolean autoParse){
        super(crawlPath, autoParse);

    }
    public LoaderCrawler(String[] urls, String crawlPath, boolean autoParse) throws SQLException, ClassNotFoundException {
        this(crawlPath, autoParse);

        orginUrls = urls;
        //init database;
        store = StorePool.getInst();

        if(orginUrls!=null) {
            for (String url : urls) {
                if (!store.isLoaded(url)) {
                    addSeed(url);

               /*fetch url like http://news.youdomain.com/xxxxx*/
                    this.addRegex(url + ".*");

                    System.out.println("add regex " + url + ".*");
              /*do not fetch url like http://news.youdomain.com/xxxx/xxx)*/

//              this.addRegex("-" + url + "/.+/.*");
//              System.out.println("add regex  -" + url + "/.+/.*");
                }
            }
        }
        /*do not fetch jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        this.addRegex("-.*#.*");

    }


    public void visit(Page page, CrawlDatums next) {
        String url = page.getUrl();
        try {
            if(store.isLoaded(url)) {
                return;
            }
            Document doc = page.getDoc();
            if(!store.isLoaded(url)) {
                store.loaded(doc.title(), url);
            }


            System.out.println("Visit:---->" + url);

            if (Pattern.matches(".*aspx", url)) {

                Elements el = doc.select("div[class=l-t-list]");
                if(el!=null && el.size()>0)
                {
                    String content = el.first().html();
                    content = content.replaceAll("<br/>", "\n").replaceAll("<br />", "\n");
                    content = htmlRemoveTag(content);
//        System.out.println("content:" + content);
                    if (!store.isExistData(url)) {
                        store.setData(doc.title(), url, content);
                    }
                }
            }

            long count = store.getDataCount();
            System.out.println("------- Visit:"  + count);
            if(count>10000){
                System.out.println("----------------------------- ALL  DONE ----------------------------" );
                System.exit(0);
            }


            String text = page.getDoc().select("a").toString();
            //System.out.println(text);
            List<String> urls = null;
            if(orginUrls!=null) {
                urls = find(orginUrls, text);
            }
            if(urls!= null && urls.size()>0){
                for(String u : urls){
                    synchronized (urlsSet) {

                        if (!urlsSet.contains(u) && !store.isLoaded(u)){
                            System.out.println("==== add url:" + u);
                            urlsSet.add(u);
                            next.add(u);
                        }

                    }
                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String htmlRemoveTag(String inputString) {
        if (inputString == null)
            return null;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll("\n"); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll("\n"); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll("\n"); // 过滤html标签
            textStr = htmlStr.replaceAll("&nbsp;", " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textStr;// 返回文本字符串
    }

    public List<String> find(String[] loadedUrls, String text){

        List list = new ArrayList();
//        String pattern= "^http\:\/\/.+$";
        String pattern = "http.*aspx";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        String s;
        while(m.find()) {
            s = m.group();
           // System.out.println("url="+s);
            list.add(s);
        }
        pattern = "href=\"(.*?)\"";
        p = Pattern.compile(pattern);
        m = p.matcher(text);
        while(m.find()) {
             s = m.group(1);
            //System.out.println("url="+s);
             for(String u : loadedUrls){
                 String path = getPath(u);
                 if(s.contains(path) && !s.equals(path)){
                     if(s.startsWith("/")){
                         String b = getBasePath(u) + s;
                         list.add(b);
                     }

                 }
             }

        }
        return list;
    }


    /**
     * url ： regexpqyfkh_left$& ' ].
     protocol:RegExp.$2,
     host:RegExp.$3,
     path:RegExp.$4,
     file:RegExp.$6,
     query:RegExp.$7,
     hash:RegExp.$8
     * @param url
     * @return
     */
    private String getPath(String url){
        String pattern = "((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(url);
        if(m.find()){
            return m.group(4);
        }
        return null;
    }

    private String getBasePath(String url){
        String pattern = "((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(url);
        if(m.find()){
            return m.group(2) + "://" + m.group(3) ;
        }
        return null;
    }

}

