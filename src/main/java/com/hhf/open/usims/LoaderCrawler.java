package com.hhf.open.usims;

import cn.edu.hfut.dmic.webcollector.crawler.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/6/23.
 */
public class LoaderCrawler extends BreadthCrawler {

    StorePool pool;
    public LoaderCrawler(String[] urls, String crawlPath, boolean autoParse) throws SQLException, ClassNotFoundException {
        super(crawlPath, autoParse);

        for(String url : urls){
            addSeed(url);

           /*fetch url like http://news.youdomain.com/xxxxx*/
            this.addRegex( url +".*");
            System.out.println("add regex " + url +".*");
             /*do not fetch url like http://news.youdomain.com/xxxx/xxx)*/
            this.addRegex("-" + url +"/.+/.*");
            System.out.println("add regex  -" +  url +"/.+/.*");
        }

        /*do not fetch jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        this.addRegex("-.*#.*");

        //init database;
        pool = new StorePool();
    }


    public void visit(Page page, Links links) {
        String url = page.getUrl();
        System.out.println("Visit:---->" + url);
        if (Pattern.matches(".*aspx", url)) {
            Document doc = page.getDoc();
            String content = doc.select("div[class=l-t-list]").first().html();
        content = content.replaceAll("<br/>","\n").replaceAll("<br />", "\n");
        content = htmlRemoveTag(content);
//        System.out.println("content:" + content);
//        System.out.println("Visit:---------------------------------------------------------" );
            if(!pool.isLoaded(url)) {
                pool.setData(url, content);
            }
        }

        long count = pool.getDataCount();
        System.out.println("------- Visit:"  + count);
        if(count>10000){
            System.out.println("----------------------------- DONE ----------------------------" );
            System.exit(0);
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
}

