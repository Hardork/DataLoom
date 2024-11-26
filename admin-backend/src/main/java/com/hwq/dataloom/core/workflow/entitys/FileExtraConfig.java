package com.hwq.dataloom.core.workflow.entitys;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Min;

import com.hwq.dataloom.core.workflow.enums.FileTransferMethod;
import com.hwq.dataloom.core.workflow.enums.FileType;
import lombok.Data;

/**
 * 文件配置类
 */
@Data
public class FileExtraConfig {

    // 允许的文件类型列表，allowed_file_types，默认初始化为空列表
    private List<FileType> allowedFileTypes = new ArrayList<>();

    // 允许的文件扩展名列表，allowed_extensions，默认初始化为空列表
    private List<String> allowedExtensions = new ArrayList<>();

    // 允许的文件上传方法列表，allowed_upload_methods，默认初始化为空列表
    private List<FileTransferMethod> allowedUploadMethods = new ArrayList<>();

    // 文件数量限制，number_limits，最小值设为0，通过@Min注解来约束
    private int numberLimits;

    // 可以添加一些必要的构造函数等，以下是默认构造函数示例
    public FileExtraConfig() {
    }

    // 全参构造函数示例，方便根据具体参数创建实例
    public FileExtraConfig(List<FileType> allowedFileTypes,
                           List<String> allowedExtensions, List<FileTransferMethod> allowedUploadMethods,
                           int numberLimits) {
        this.allowedFileTypes = allowedFileTypes;
        this.allowedExtensions = allowedExtensions;
        this.allowedUploadMethods = allowedUploadMethods;
        this.numberLimits = numberLimits;
    }
}