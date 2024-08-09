package com.hwq.bi.model.dto.failed_chart;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hwq.bi.common.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * @Author:HWQ
 * @DateTime:2023/10/23 16:31
 * @Description:
 **/
@Data
public class FailedChartQueryRequest extends PageRequest {

    private Long id;

    /**
     * 图表id
     */
    private Long chartId;

    /**
     * wait,running,succeed,failed
     */
    @TableField(value = "status")
    private String status;

    /**
     * 执行信息
     */
    @TableField(value = "execMessage")
    private String execMessage;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;
}
