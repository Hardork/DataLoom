package com.hwq.bi.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.model.dto.failed_chart.FailedChartQueryRequest;
import com.hwq.bi.model.dto.order.OrderQueryRequest;
import com.hwq.bi.model.entity.FailedChart;
import com.hwq.bi.service.FailedChartService;
import com.hwq.bi.mapper.FailedChartMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author HWQ
* @description 针对表【failed_chart(失败分析表)】的数据库操作Service实现
* @createDate 2023-10-23 16:19:25
*/
@Service
public class FailedChartServiceImpl extends ServiceImpl<FailedChartMapper, FailedChart>
    implements FailedChartService{

    @Override
    public QueryWrapper<FailedChart> getQueryWrapper(FailedChartQueryRequest failedChartQueryRequest) {
         Long id = failedChartQueryRequest.getId();
         Long chartId = failedChartQueryRequest.getChartId();
         String status = failedChartQueryRequest.getStatus();
         String execMessage = failedChartQueryRequest.getExecMessage();
         Long userId = failedChartQueryRequest.getUserId();
         Date createTime = failedChartQueryRequest.getCreateTime();
         Date updateTime = failedChartQueryRequest.getUpdateTime();

        QueryWrapper<FailedChart> qw = new QueryWrapper<>();
        qw.eq(ObjectUtils.isNotEmpty(id), "id", id);
        qw.eq(ObjectUtils.isNotEmpty(chartId), "chartId", chartId);
        qw.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        qw.eq(ObjectUtils.isNotEmpty(createTime), "createTime", createTime);
        qw.eq(ObjectUtils.isNotEmpty(updateTime), "updateTime", updateTime);
        qw.eq(StringUtils.isNotEmpty(status), "status", status);
        qw.eq(StringUtils.isNotEmpty(execMessage), "execMessage", execMessage);

        return qw;
    }
}




