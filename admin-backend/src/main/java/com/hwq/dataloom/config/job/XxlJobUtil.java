package com.hwq.dataloom.config.job;

import com.alibaba.fastjson.JSONObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class XxlJobUtil {
    private static String cookie = "";


    /**
     * 查询现有的任务
     * @param url
     * @param requestInfo
     * @return
     * @throws IOException
     */
    public static JSONObject pageList(String url, JSONObject requestInfo) throws IOException, ParseException {
        String path = "/api/jobinfo/pageList";
        String targetUrl = url + path;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(targetUrl);
            post.setHeader("cookie", cookie);
            StringEntity requestEntity = new StringEntity(requestInfo.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                return getJsonObject(response);
            }
        }
    }

    /**
     * 新增/编辑任务
     * @param url
     * @param requestInfo
     * @return
     * @throws IOException
     */
    public static JSONObject addJob(String url, JSONObject requestInfo) throws IOException, ParseException {
        String path = "/api/jobinfo/save";
        String targetUrl = url + path;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(targetUrl);
            post.setHeader("cookie", cookie);
            StringEntity requestEntity = new StringEntity(requestInfo.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                return getJsonObject(response);
            }
        }
    }

    /**
     * 删除任务
     * @param url
     * @param id
     * @return
     * @throws IOException
     */
    public static JSONObject deleteJob(String url, int id) throws IOException, ParseException {
        String path = "/api/jobinfo/delete?id=" + id;
        return doGet(url, path);
    }

    /**
     * 开始任务
     * @param url
     * @param id
     * @return
     * @throws IOException
     */
    public static JSONObject startJob(String url, int id) throws IOException, ParseException {
        String path = "/api/jobinfo/start?id=" + id;
        return doGet(url, path);
    }

    /**
     * 停止任务
     * @param url
     * @param id
     * @return
     * @throws IOException
     */
    public static JSONObject stopJob(String url, int id) throws IOException, ParseException {
        String path = "/api/jobinfo/stop?id=" + id;
        return doGet(url, path);
    }

    public static JSONObject doGet(String url, String path) throws IOException, ParseException {
        String targetUrl = url + path;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(targetUrl);
            get.setHeader("cookie", cookie);
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                return getJsonObject(response);
            }
        }
    }

    private static JSONObject getJsonObject(CloseableHttpResponse response) throws IOException, ParseException {
        if (response.getCode() == 200) {
            String responseString = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(responseString);
        } else {
            return null;
        }
    }

    /**
     * 登录
     * @param url
     * @param userName
     * @param password
     * @return
     * @throws IOException
     */
    public static String login(String url, String userName, String password) throws IOException {
        String path = "/api/jobinfo/login?userName=" + userName + "&password=" + password;
        String targetUrl = url + path;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(targetUrl);
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                if (response.getCode() == 200) {
                    StringBuilder tmpcookies = new StringBuilder();
                    for (Header header : response.getHeaders("Set-Cookie")) {
                        tmpcookies.append(header.getValue()).append(";");
                    }
                    cookie = tmpcookies.toString();
                } else {
                    cookie = "";
                }
                return cookie;
            }
        }
    }
}
