package com.hhf.open.usims;

import cn.edu.hfut.dmic.webcollector.crawler.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import org.jsoup.nodes.Document;

import java.util.regex.Pattern;

import static com.hhf.open.usims.Config.getUrls;

/**
 * Created by Administrator on 2016/6/23.
 */
public class LoaderCrawler extends BreadthCrawler {
    public LoaderCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);

        String[] urls = getUrls();
        assertNotEmpty("Get Urls", urls);
        for(String url : urls){
//            addRegex(url);
            addSeed(url);
        }


    }

    private void assertNotEmpty(String s, String[] urls) {
    }

    public void visit(Page page, Links links) {
        String url = page.getUrl();
        System.out.println("Visit:---->" + url);

        Document doc = page.getDoc();
        String content = doc.select("div[class=l-t-list]").first().html();
//        content = content.replaceAll("<br/>","\n").replaceAll("<br />", "\n");
        content = htmlRemoveTag(content);
//        System.out.println("content:" + content);
//        System.out.println("Visit:---------------------------------------------------------" );
    }

    public String htmlRemoveTag(String inputString) {
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

