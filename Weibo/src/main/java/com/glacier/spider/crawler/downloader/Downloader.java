package com.glacier.spider.crawler.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Created by glacier on 14-12-17.
 */
public class Downloader {

    private static Logger logger = Logger.getLogger(Downloader.class.getName());
    private static DefaultHttpClient httpClient;
    public static String HTTP_GET = "get";
    public static String HTTP_POST = "post";

    public static void setClient(DefaultHttpClient client) {
        httpClient = client;
    }

    public static Document document(String url, String method) {
        try {
            HttpResponse response = null;
            if ( method.equals("get") ) {
                HttpGet httpGet = new HttpGet(url);
                httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
                httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
                httpGet.getParams().setBooleanParameter("http.tcp.nodelay", true);
                httpGet.getParams().setParameter("http.connection.stalecheck", false);
                httpGet.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
                response = httpClient.execute(httpGet);
            }
            else if ( method.equals("post") ) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
                httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
                httpPost.getParams().setBooleanParameter("http.tcp.nodelay", true);
                httpPost.getParams().setParameter("http.connection.stalecheck", false);
                httpPost.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
                response = httpClient.execute(httpPost);
            }
            HttpEntity entity = response.getEntity();
            Document document = Jsoup.parse(getContent(entity, "UTF-8"));
            document.setBaseUri(url);
            return document;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    private static String getContent(HttpEntity entity, String encode) {
        BufferedReader reader = null;
        StringBuffer buffer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), encode));
            buffer = new StringBuffer();
            String temp = null;
            while( (temp = reader.readLine()) != null ) {
                buffer.append(temp);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}