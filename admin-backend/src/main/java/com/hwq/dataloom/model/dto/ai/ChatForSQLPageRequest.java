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
     * 模型Id
     */
    private Long chatId;

    private String sql;

    private Integer page;

    private Integer size;

}
