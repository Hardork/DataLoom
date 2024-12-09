package com.hwq.dataloom.core.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwq.dataloom.core.workflow.config.FileExtraConfig;
import com.hwq.dataloom.core.workflow.enums.FileTransferMethod;
import com.hwq.dataloom.core.workflow.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: DataLoom文件类
 * @DateTime: 2024/11/25 15:59
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {

    private String id; // message file id
    private Long userId;
    private FileType type;
    private FileTransferMethod transferMethod;
    private String remoteUrl; // remote url
    private String relatedId;
    private String fileName;
    private String extension; // File extension, should contains dot
    private String mimeType;
    private int size = -1;
    private FileExtraConfig extraConfig;

    public Map<String, Object> toMap() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.convertValue(this, Map.class);
        Map<String, Object> result = new HashMap<>();
        result.putAll(data);
        result.put("url", generateUrl());
        return result;
    }

    // 这里假设generateUrl是一个自定义的方法，用于生成文件对应的url，需要根据实际情况实现具体逻辑
    private String generateUrl() {
        // TODO: 返回文件对应的url逻辑，这里简单返回空字符串示例，实际要完善
        return "";
    }

    public String getMarkdown() {
        String url = generateUrl();
        FileType fileType = getType();
        if (fileType == FileType.IMAGE) {
            return String.format("![%s](%s)", getFileName() != null? getFileName() : "", url);
        } else {
            return String.format("[%s](%s)", getFileName()!= null? getFileName() : url, url);
        }
    }
}