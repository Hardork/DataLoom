package com.hwq.dataloom.core.workflow.entitys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;

import com.hwq.dataloom.core.workflow.enums.FileTransferMethod;
import com.hwq.dataloom.core.workflow.enums.FileType;
import lombok.Builder;
import lombok.Data;

/**
 * 文件配置类
 */
@Data
@Builder
public class FileExtraConfig {

    // 允许的文件类型列表，allowed_file_types，默认初始化为空列表
    private List<FileType> allowedFileTypes = new ArrayList<>();

    // 允许的文件扩展名列表，allowed_extensions，默认初始化为空列表
    private List<String> allowedExtensions = new ArrayList<>();

    // 允许的文件上传方法列表，allowed_upload_methods，默认初始化为空列表
    private List<FileTransferMethod> allowedUploadMethods = new ArrayList<>();

    // 文件数量限制，number_limits，最小值设为0，通过@Min注解来约束
    private int numberLimits;

    public static FileExtraConfig modelValidate(Map<String, Object> imageConfig) {
        int numberLimits =  (int) imageConfig.get("numberLimits");
        List<String> transferMethods = (List<String>) imageConfig.get("transferMethods");
        if (transferMethods == null) {
            transferMethods = new ArrayList<>();
        }
        List<FileTransferMethod> transferMethodList = transferMethods.stream()
                .map(FileTransferMethod::fromValue)
                .collect(Collectors.toList());
        return FileExtraConfig.builder()
                .numberLimits(numberLimits)
                .allowedUploadMethods(transferMethodList)
                .build();
    }
}