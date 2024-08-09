package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.model.entity.ServiceRecord;
import com.hwq.dataloom.mapper.ServiceRecordMapper;
import com.hwq.dataloom.model.entity.User;
import com.hwq.dataloom.model.enums.ServiceTypeEnums;
import com.hwq.dataloom.model.vo.GetCurMonthServiceRecordVO;
import com.hwq.dataloom.service.ServiceRecordService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author HWQ
* @description 针对表【service_record(服务调用记录表)】的数据库操作Service实现
* @createDate 2023-09-29 15:57:25
*/
@Service
public class ServiceRecordServiceImpl extends ServiceImpl<ServiceRecordMapper, ServiceRecord>
    implements ServiceRecordService {

    @Override
    public GetCurMonthServiceRecordVO getUserCurMonthBiRecord(User loginUser) {
        // 获取当前月份的起始和结束时间
        LocalDate currentMonth = LocalDate.now();
        LocalDate startOfMonth = currentMonth.withDayOfMonth(1);
        LocalDate endOfMonth = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
        QueryWrapper<ServiceRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ServiceRecord::getType, ServiceTypeEnums.BI.getValue())
                .eq(ServiceRecord::getUserId, loginUser.getId())
                .ge(ServiceRecord::getCreateTime, startOfMonth)
                .le(ServiceRecord::getCreateTime, endOfMonth)
                .orderByAsc(ServiceRecord::getCreateTime)
        ;
        List<ServiceRecord> records = this.list(queryWrapper);
        LinkedHashMap<String, Long> collect = records.stream().collect(Collectors.groupingBy((serviceRecord) -> {
            Date createTime = serviceRecord.getCreateTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(createTime);
        }, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        // 分组每一天的调用次数
        GetCurMonthServiceRecordVO getCurMonthServiceRecordVO = new GetCurMonthServiceRecordVO();
        getCurMonthServiceRecordVO.setServiceType(ServiceTypeEnums.BI.getText());
        ArrayList<Long> serviceData = new ArrayList<>();
        ArrayList<String> serviceDate = new ArrayList<>();
        for (String date : collect.keySet()) {
            serviceDate.add(date);
            Long dayCount = collect.get(date);
            serviceData.add(dayCount);
        }
        getCurMonthServiceRecordVO.setServiceData(serviceData);
        getCurMonthServiceRecordVO.setServiceDate(serviceDate);
        return getCurMonthServiceRecordVO;
    }

    @Override
    public GetCurMonthServiceRecordVO getUserCurMonthAiRecord(User loginUser) {
        // 获取当前月份的起始和结束时间
        LocalDate currentMonth = LocalDate.now();
        LocalDate startOfMonth = currentMonth.withDayOfMonth(1);
        LocalDate endOfMonth = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
        // 构造查询条件
        QueryWrapper<ServiceRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ServiceRecord::getType, ServiceTypeEnums.AI.getValue())
                .eq(ServiceRecord::getUserId, loginUser.getId())
                .ge(ServiceRecord::getCreateTime, startOfMonth)
                .le(ServiceRecord::getCreateTime, endOfMonth)
                .orderByAsc(ServiceRecord::getCreateTime)
        ;

        List<ServiceRecord> records = this.list(queryWrapper);
        LinkedHashMap<String, Long> collect = records.stream().collect(Collectors.groupingBy((serviceRecord) -> {
            Date createTime = serviceRecord.getCreateTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(createTime);
        }, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        // 分组每一天的调用次数
        GetCurMonthServiceRecordVO getCurMonthServiceRecordVO = new GetCurMonthServiceRecordVO();
        getCurMonthServiceRecordVO.setServiceType(ServiceTypeEnums.AI.getText());
        ArrayList<Long> serviceData = new ArrayList<>();
        ArrayList<String> serviceDate = new ArrayList<>();
        for (String date : collect.keySet()) {
            serviceDate.add(date);
            Long dayCount = collect.get(date);
            serviceData.add(dayCount);
        }
        getCurMonthServiceRecordVO.setServiceData(serviceData);
        getCurMonthServiceRecordVO.setServiceDate(serviceDate);
        return getCurMonthServiceRecordVO;
    }
}




