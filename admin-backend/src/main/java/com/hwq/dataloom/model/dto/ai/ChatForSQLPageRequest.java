package com.hwq.dataloom.model.dto.ai;

import lombok.Data;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/29
 * @Description: 智能问答分页查询
 **/
@Data
public class ChatForSQLPageRequest {


    /**
     * 当前对话Id
     */
    private Long chatHistoryId;

    private Integer pageNo;

    private Integer size;

}
