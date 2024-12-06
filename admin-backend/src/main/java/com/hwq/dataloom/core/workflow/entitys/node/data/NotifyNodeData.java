package com.hwq.dataloom.core.workflow.entitys.node.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotifyNodeData extends BaseNodeData {
    /**
     * 通知类型
     */
    private String notifyType;

    /**
     * 通知邮箱
     */

    private String notifyEmail;

    /**
     * 通知内容（支持变量插入）
     */
    private String notifyContent;

    /**
     * 通知标题
     */
    private String notifyTitle;
}
