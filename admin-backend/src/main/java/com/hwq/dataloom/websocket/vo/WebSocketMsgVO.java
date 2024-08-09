package com.hwq.dataloom.websocket.vo;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/24 15:14
 * @Description:
 **/
@Data
public class WebSocketMsgVO {
    private String type;
    private String title;
    private String description;
    private String chartId;
}
