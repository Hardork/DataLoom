package com.hwq.dataloom.service;

import com.hwq.dataloom.model.entity.ServiceRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.entity.User;
import com.hwq.dataloom.model.vo.GetCurMonthServiceRecordVO;

/**
* @author HWQ
* @description 针对表【service_record(服务调用记录表)】的数据库操作Service
* @createDate 2023-09-29 15:57:25
*/
public interface ServiceRecordService extends IService<ServiceRecord> {

    /**
     * 获取用户当前月份BI服务调用情况
     * @return
     */
    GetCurMonthServiceRecordVO getUserCurMonthBiRecord(User loginUser);

    /**
     * 获取用户当前月份Ai服务调用情况
     * @param loginUser
     * @return
     */
    GetCurMonthServiceRecordVO getUserCurMonthAiRecord(User loginUser);
}
