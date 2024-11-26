package com.hwq.dataloom.core.workflow.entitys;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
/**
 * @Author: HWQ
 * @Description: 工作流额外配置类
 * @DateTime: 2024/11/26 17:01
 **/
@Data
public class WorkflowAdditionalFeatures {
    // 文件上传相关配置，file_upload，可为空
    private FileExtraConfig fileUpload;

    // 开场白，opening_statement，可为空
    private String openingStatement;

    // 建议问题列表，suggested_questions，默认初始化为空列表
    private List<String> suggestedQuestions = new ArrayList<>();

    // 回答后是否显示建议问题，suggested_questions_after_answer，默认值为false
    private boolean suggestedQuestionsAfterAnswer = false;

    // 是否显示检索来源，show_retrieve_source，默认值为false
    private boolean showRetrieveSource = false;

    // 是否显示更多类似内容，more_like_this，默认值为false
    private boolean moreLikeThis = false;



    // 可以添加一些必要的构造函数、方法等，以下是一个示例构造函数
    public WorkflowAdditionalFeatures() {
    }

    public WorkflowAdditionalFeatures(FileExtraConfig fileUpload, String openingStatement, List<String> suggestedQuestions,
                                 boolean suggestedQuestionsAfterAnswer, boolean showRetrieveSource,
                                 boolean moreLikeThis) {
        this.fileUpload = fileUpload;
        this.openingStatement = openingStatement;
        this.suggestedQuestions = suggestedQuestions;
        this.suggestedQuestionsAfterAnswer = suggestedQuestionsAfterAnswer;
        this.showRetrieveSource = showRetrieveSource;
        this.moreLikeThis = moreLikeThis;
    }
}
