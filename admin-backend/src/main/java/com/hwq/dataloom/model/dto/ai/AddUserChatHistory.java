package com.hwq.dataloom.model.dto.ai;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/10/4 2:37
 * @Description:
 **/
@Data
public class AddUserChatHistory {
    /**
     * 助手id
     */
    @TableField(value = "modelId")
    private Long modelId;
}
