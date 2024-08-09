package com.hwq.bi.model.dto.user_massage;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 11:04
 * @Description:
 **/
@Data
public class UserMessageAddRequest {

    private Long userId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 内容
     */
    private String description;

    /**
     * 0-普通 1-成功 2-失败
     */
    private Integer type;

    /**
     * 消息对应跳转的路由
     */
    private String route;

    private Integer isRead;


    private static final long serialVersionUID = 1L;
}
