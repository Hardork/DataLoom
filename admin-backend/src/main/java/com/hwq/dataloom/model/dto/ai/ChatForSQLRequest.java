package com.hwq.dataloom.model.dto.ai;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/6/17 23:38
 * @description
 */
@Data
public class ChatForSQLRequest {
    /**
     * 模型Id
     */
    private Long chatId;
    /**
     * 询问的数据
     */
    private String question;
}
