package com.hwq.dataloom.model.dto.ai_role;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hwq.dataloom.framework.request.PageRequest;
import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/27 15:30
 * @Description:
 **/
@Data
public class AiRoleQueryRequest extends PageRequest {

    private Long id;

    /**
     * 助手名称
     */
    @TableField(value = "assistantName")
    private String assistantName;


    /**
     * 助手类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 历史对话
     */
    @TableField(value = "historyTalk")
    private Integer historyTalk;

    /**
     * 功能描述
     */
    @TableField(value = "functionDes")
    private String functionDes;

    /**
     * 输入模型
     */
    @TableField(value = "inputModel")
    private String inputModel;

    /**
     * 角色设定
     */
    @TableField(value = "roleDesign")
    private String roleDesign;

    /**
     * 目标任务
     */
    @TableField(value = "targetWork")
    private String targetWork;

    /**
     * 需求说明
     */
    @TableField(value = "requirement")
    private String requirement;

    /**
     * 风格设定
     */
    @TableField(value = "style")
    private String style;

    /**
     * 其它示例
     */
    @TableField(value = "otherRequire")
    private String otherRequire;

    /**
     * 0-助手市场 1-我的助手
     */
    private Integer selectKey;

}
