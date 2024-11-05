package com.hwq.dataloom.model.dto.ai;

import com.hwq.dataloom.framework.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/11/3 22:38
 * @description 智能分析图表请求类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisChartByAIRequest {
    /**
     * 任务是否执行成功
     */
    private Boolean isSuccess = Boolean.TRUE;

    /**
     * 用户提问的问题
     */
    private String question;

    /**
     * 当前层结果
     */
    private String res;

    /**
     * 用户信息
     */
    private User userInfo;
}
