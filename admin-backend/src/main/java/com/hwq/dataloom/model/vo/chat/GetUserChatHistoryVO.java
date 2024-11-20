package com.hwq.dataloom.model.vo.chat;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/10/4 2:26
 * @Description: 获取用户历史对话
 **/
@Data
public class GetUserChatHistoryVO {

    /**
     * 对话id
     */
    private Long chatId;

    /**
     * 助手名称
     */
    private String assistantName;


    /**
     * 助手功能描述
     */
    private String functionDes;

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源类型
     */
    private String datasourceType;
}
