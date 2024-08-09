package com.hwq.bi.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/10/4 2:26
 * @Description:
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
}
