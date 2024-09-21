package com.hwq.dataloom.websocket.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 21:37
 * @Description:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiWebSocketVO {
    private String content;
    private String type;
}
