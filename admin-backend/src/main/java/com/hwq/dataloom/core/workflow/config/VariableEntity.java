package com.hwq.dataloom.core.workflow.config;

/**
 * @Author: HWQ
 * @Description:
 * @DateTime: 2024/11/26 16:51
 **/
import com.hwq.dataloom.core.workflow.enums.FileTransferMethod;
import com.hwq.dataloom.core.workflow.enums.FileType;
import com.hwq.dataloom.core.workflow.enums.VariableEntityType;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 变量规则实体类
 */
@Data
@Builder
public class VariableEntity {
    // 变量名，variable
    @NotBlank(message = "变量不能为空")
    private String variable;

    // 标签，label
    @NotBlank(message = "标签不能为空")
    private String label;

    // 描述，description，默认值设为空字符串
    private String description = "";

    // 类型，type
    @NotNull(message = "类型不能为空")
    private VariableEntityType type;

    // 是否必填，required，默认值设为false
    private boolean required = false;

    // 最大长度，max_length
    private Integer maxLength;

    // 选项列表，options，默认值设为空列表
    private List<String> options = new ArrayList<>();

    // 允许的文件类型列表，allowed_file_types，默认值设为空列表
    private List<FileType> allowedFileTypes = new ArrayList<>();

    // 允许的文件扩展名列表，allowed_file_extensions，默认值设为空列表
    private List<String> allowedFileExtensions = new ArrayList<>();

    // 允许的文件上传方法列表默认值设为空列表
    private List<FileTransferMethod> allowedFileUploadMethods = new ArrayList<>();

    // convert_none_description方法的逻辑，确保description不为null，返回空字符串替代null
    public static String convertNoneDescription(String v) {
        return v!= null? v : "";
    }

    // convert_none_options方法的逻辑，确保options不为null，返回空列表替代null
    public static List<String> convertNoneOptions(List<String> v) {
        return v!= null? v : new ArrayList<>();
    }

    public static VariableEntity convertMap2Entity(Map<String, Object> variableConfig) {
        // 获取变量名，若不存在则抛出异常，实际中可根据业务决定如何处理异常情况
        String variable = (String) variableConfig.get("variable");
        if (variable == null) {
            throw new IllegalArgumentException("变量名不能为空");
        }

        // 获取标签，同理处理
        String label = (String) variableConfig.get("label");
        if (label == null) {
            throw new IllegalArgumentException("标签不能为空");
        }

        // 获取类型，转换为VariableEntityType类型，这里假设VariableEntityType有合适的构造方法或者工厂方法来从Object转换
        VariableEntityType type = null;
        Object typeObj = variableConfig.get("type");
        if (typeObj != null) {
            type = VariableEntityType.fromValue((String) typeObj); // 假设存在这样的转换方法
        } else {
            throw new IllegalArgumentException("类型不能为空");
        }

        // 处理描述，调用convertNoneDescription方法确保不为null
        String description = ((String) variableConfig.get("description"));
        description = VariableEntity.convertNoneDescription(description);

        // 处理是否必填字段，获取布尔值，若不存在默认为false
        boolean required = false;
        if (variableConfig.containsKey("required")) {
            required = (boolean) variableConfig.get("required");
        }

        // 处理最大长度字段，获取整数值，可为空
        Integer maxLength = (Integer) variableConfig.get("maxLength");

        // 处理选项列表，调用convertNoneOptions方法确保不为null
        List<String> options;
        try {
            options = (List<String>) variableConfig.get("options");
        } catch (ClassCastException e) {
            options = new ArrayList<>();
        }

        // 处理允许的文件类型列表，这里假设FileType也有合适的方法从Object转换，可为空
        List<FileType> allowedFileTypes = new ArrayList<>();
        Object fileTypesObj = variableConfig.get("allowedFileTypes");
        if (fileTypesObj != null && fileTypesObj instanceof List) {
            List<Object> fileTypeObjs = (List<Object>) fileTypesObj;
            for (Object fileTypeObj : fileTypeObjs) {
                FileType fileType = FileType.fromValue((String) fileTypeObj);
                allowedFileTypes.add(fileType);
            }
        }

        // 处理允许的文件扩展名列表，可为空
        List<String> allowedFileExtensions = (List<String>) variableConfig.get("allowedFileExtensions");
        allowedFileExtensions = VariableEntity.convertNoneOptions(allowedFileExtensions);

        // 处理允许的文件上传方法列表，假设FileTransferMethod有对应转换方法，可为空
        List<FileTransferMethod> allowedFileUploadMethods = new ArrayList<>();
        Object uploadMethodsObj = variableConfig.get("allowedFileUploadMethods");
        if (uploadMethodsObj instanceof List) {
            List<Object> uploadMethodObjs = (List<Object>) uploadMethodsObj;
            for (Object uploadMethodObj : uploadMethodObjs) {
                FileTransferMethod fileTransferMethod = FileTransferMethod.fromValue((String) uploadMethodObj); // 假设存在这样的转换方法
                allowedFileUploadMethods.add(fileTransferMethod);
            }
        }

        return VariableEntity.builder()
                .variable(variable)
                .label(label)
                .description(description)
                .type(type)
                .required(required)
                .maxLength(maxLength)
                .options(options)
                .allowedFileTypes(allowedFileTypes)
                .allowedFileExtensions(allowedFileExtensions)
                .allowedFileUploadMethods(allowedFileUploadMethods)
                .build();
    }

}
