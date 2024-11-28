package com.hwq.dataloom.core.workflow.config.features;

import cn.hutool.core.map.MapUtil;
import com.hwq.dataloom.core.workflow.config.FileExtraConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 将配置信息转换为对应的文件上传相关配置对象
 * @DateTime: 2024/11/26 16:40
 **/
public class FileUploadConfigManager {

    /**
     * 将配置信息转换为对应的文件上传相关配置对象
     *
     * @param config 配置信息的映射结构，对应Python中的字典类型参数
     * @param isVision 布尔值，用于判断是否是视觉相关特性（对应Python中的is_vision参数）
     * @return FileExtraConfig类型的对象，表示转换后的文件上传配置，如果不符合条件则返回null
     */
    public static FileExtraConfig convert(Map<String, Object> config, boolean isVision) {
        Map<String, Object> fileUploadDict = (Map<String, Object>) config.get("fileUpload");
        if (fileUploadDict != null && (boolean) fileUploadDict.get("enabled")) {
            List<String> transformMethods = (List<String>) fileUploadDict.get("allowedFileUploadMethods");
            if (transformMethods == null) {
                transformMethods = (List<String>) fileUploadDict.get("allowedUploadMethods");
                if (transformMethods == null) {
                    transformMethods = new ArrayList<>();
                }
            }

            Map<String, Object> imageConfig = MapUtil.<String, Object>builder()
                    .put("numberLimits", fileUploadDict.get("numberLimits"))
                    .put("transferMethods", transformMethods)
                    .build();

            if (isVision) {
                Map<String, Object> imageDetailConfig = (Map<String, Object>) fileUploadDict.get("image");
                if (imageDetailConfig == null) {
                    imageDetailConfig = MapUtil.empty();
                }
                String detail = (String) imageDetailConfig.get("detail");
                if (detail == null) {
                    detail = "low";
                }
                imageConfig.put("detail", detail);
            }

            return FileExtraConfig.modelValidate(
                    MapUtil.<String, Object>builder()
                    .put("imageConfig", imageConfig)
                    .build()
            );
        }

        return null;
    }
}