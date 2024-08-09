package com.hwq.dataloom.model.dto.ai;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 21:13
 * @Description:
 **/
@Data
public class ChatWithModelRequest {
    private String text;
    private Long chatId;
}
