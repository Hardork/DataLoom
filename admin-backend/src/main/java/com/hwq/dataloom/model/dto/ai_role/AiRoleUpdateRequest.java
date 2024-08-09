package com.hwq.dataloom.model.dto.ai_role;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/27 15:27
 * @Description:
 **/
@Data
public class AiRoleUpdateRequest {
    /**
     *
     */
    private Long id;

    /**
     * 助手名称
     */
    private String assistantName;

    /**
     * 助手类型
     */
    private String type;

    /**
     * 历史对话
     */
    private Integer historyTalk;

    /**
     * 功能描述
     */
    private String functionDes;

    /**
     * 输入模型
     */
    private String inputModel;

    /**
     * 角色设定
     */
    private String roleDesign;

    /**
     * 目标任务
     */
    private String targetWork;

    /**
     * 需求说明
     */
    private String requirement;

    /**
     * 风格设定
     */
    private String style;

    /**
     * 其它示例
     */
    private String otherRequire;

}
