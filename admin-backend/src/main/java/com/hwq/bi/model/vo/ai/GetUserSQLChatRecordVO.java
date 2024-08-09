package com.hwq.bi.model.vo.ai;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/19 00:54
 * @description
 */
@Data
public class GetUserSQLChatRecordVO {
    /**
     *
     */
    private Long id;

    /**
     * 0-用户 1-AI
     */
    private Integer chatRole;

    /**
     * 内容
     */
    private String content;

    /**
     * 列
     */
    private List<ColumnsVO> columns;

    /**
     * 数据
     */
    private List<Map<String, Object>> res;

    /**
     * 对应的sql
     */
    private String sql;

    /**
     * 会话id
     */
    private Long chatId;

    /**
     * 助手id
     */
    private Long modelId;


    /**
     * 消息状态  0-正常 1-异常
     */
    private Integer status;
}
