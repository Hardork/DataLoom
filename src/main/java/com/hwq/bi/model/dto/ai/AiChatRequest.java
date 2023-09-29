package com.hwq.bi.model.dto.ai;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 21:13
 * @Description:
 **/
@Data
public class AiChatRequest {
    private String text;
    private Long assistantId;
}
