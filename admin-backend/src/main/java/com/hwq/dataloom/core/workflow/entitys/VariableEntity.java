package com.hwq.dataloom.core.workflow.entitys;

/**
 * @Author: HWQ
 * @Description:
 * @DateTime: 2024/11/26 16:51
 **/
import com.hwq.dataloom.core.workflow.enums.FileTransferMethod;
import com.hwq.dataloom.core.workflow.enums.FileType;
import com.hwq.dataloom.core.workflow.enums.VariableEntityType;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 变量规则实体类
 */
@Data
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
    public String convertNoneDescription(String v) {
        return v!= null? v : "";
    }

    // convert_none_options方法的逻辑，确保options不为null，返回空列表替代null
    public List<String> convertNoneOptions(List<String> v) {
        return v!= null? v : new ArrayList<>();
    }
}
