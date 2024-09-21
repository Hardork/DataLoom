package com.hwq.dataloom.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.websocket.ChartAnalysisWebSocket;
import com.hwq.dataloom.websocket.vo.AiWebSocketVO;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.Optional;


/**
 * KimiAI
 */
@Component
@ConfigurationProperties(prefix = "kimi")
@Data
public class MoonshotAiClient {

    private String apiKey;
    private String modelsUrl;
    private String filesUrl;
    private String estimateTokenCountUrl;
    private String chatCompletionUrl;

    @Resource
    private ChartAnalysisWebSocket chartAnalysisWebSocket;

    public String getModelList() {
        return getCommonRequest(modelsUrl)
                .execute()
                .body();
    }

    public String uploadFile(@NonNull File file) {
        return getCommonRequest(filesUrl)
                .method(Method.POST)
                .header("purpose", "file-extract")
                .form("file", file)
                .execute()
                .body();
    }

    public String getFileList() {
        return getCommonRequest(filesUrl)
                .execute()
                .body();
    }

    public String deleteFile(@NonNull String fileId) {
        return getCommonRequest(filesUrl + "/" + fileId)
                .method(Method.DELETE)
                .execute()
                .body();
    }

    public String getFileDetail(@NonNull String fileId) {
        return getCommonRequest(filesUrl + "/" + fileId)
                .execute()
                .body();
    }

    public String getFileContent(@NonNull String fileId) {
        return getCommonRequest(filesUrl + "/" + fileId + "/content")
                .execute()
                .body();
    }

    public String estimateTokenCount(@NonNull String model, @NonNull List<Message> messages) {
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .toString();
        return getCommonRequest(estimateTokenCountUrl)
                .method(Method.POST)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(requestBody)
                .execute()
                .body();
    }


    /**
     * 流式返回结果
     * @param model
     * @param messages
     * @param loginUser
     * @return
     */
    @SneakyThrows
    public String chatFlux(@NonNull String model, @NonNull List<Message> messages, User loginUser) {
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .putOpt("stream", true)
                .toString();
        Request okhttpRequest = new Request.Builder()
                .url(chatCompletionUrl)
                .post(RequestBody.create(requestBody, MediaType.get(ContentType.JSON.getValue())))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Call call = new OkHttpClient().newCall(okhttpRequest);
        Response okhttpResponse = call.execute();
        BufferedReader reader = new BufferedReader(okhttpResponse.body().charStream());
        String line;
        StringBuilder res = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            if (JSONUtil.isTypeJSON(line)) {
                Optional.of(JSONUtil.parseObj(line))
                        .map(x -> x.getJSONObject("error"))
                        .map(x -> x.getStr("message"))
                        .ifPresent(x -> System.out.println("error: " + x));
                // 异常
                chartAnalysisWebSocket.sendOneMessage(loginUser.getId(),
                        AiWebSocketVO.builder()
                                .type("error")
                                .build());
                return null;
            }
            line = StrUtil.replace(line, "data: ", StrUtil.EMPTY);
            if (StrUtil.equals("[DONE]", line) || !JSONUtil.isTypeJSON(line)) {
                chartAnalysisWebSocket.sendOneMessage(loginUser.getId(),
                        AiWebSocketVO.builder()
                                .type("end")
                                .build()
                );
                return res.toString();
            }

            Optional.of(JSONUtil.parseObj(line))
                    .map(x -> x.getJSONArray("choices"))
                    .filter(CollUtil::isNotEmpty)
                    .map(x -> (JSONObject) x.get(0))
                    .map(x -> x.getJSONObject("delta"))
                    .map(x -> x.getStr("content"))
                    .ifPresent(x -> {
                        chartAnalysisWebSocket.sendOneMessage(loginUser.getId(),
                                AiWebSocketVO.builder()
                                .type("running")
                                .content(x)
                                .build()
                        );
                        res.append(x);
                    });
        }
        return null;
    }

    @SneakyThrows
    public String chat(@NonNull String model, @NonNull List<Message> messages) {
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .putOpt("stream", true)
                .toString();
        Request okhttpRequest = new Request.Builder()
                .url(chatCompletionUrl)
                .post(RequestBody.create(requestBody, MediaType.get(ContentType.JSON.getValue())))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Call call = new OkHttpClient().newCall(okhttpRequest);
        Response okhttpResponse = call.execute();
        BufferedReader reader = new BufferedReader(okhttpResponse.body().charStream());
        String line;
        StringBuilder res = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            if (JSONUtil.isTypeJSON(line)) {
                Optional.of(JSONUtil.parseObj(line))
                        .map(x -> x.getJSONObject("error"))
                        .map(x -> x.getStr("message"))
                        .ifPresent(x -> System.out.println("error: " + x));
                return null;
            }
            line = StrUtil.replace(line, "data: ", StrUtil.EMPTY);
            if (StrUtil.equals("[DONE]", line) || !JSONUtil.isTypeJSON(line)) {
                return res.toString();
            }

            Optional.of(JSONUtil.parseObj(line))
                    .map(x -> x.getJSONArray("choices"))
                    .filter(CollUtil::isNotEmpty)
                    .map(x -> (JSONObject) x.get(0))
                    .map(x -> x.getJSONObject("delta"))
                    .map(x -> x.getStr("content"))
                    .ifPresent(res::append);
        }
        return null;
    }

    private HttpRequest getCommonRequest(@NonNull String url) {
        return HttpRequest.of(url).header(Header.AUTHORIZATION, "Bearer " + apiKey);
    }

}