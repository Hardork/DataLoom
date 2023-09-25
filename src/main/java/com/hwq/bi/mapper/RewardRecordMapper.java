package com.hwq.bi.mapper;

import com.hwq.bi.model.entity.RewardRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author HWQ
* @description 针对表【reward_record(奖励积分领取表)】的数据库操作Mapper
* @createDate 2023-09-22 21:40:59
* @Entity com.hwq.bi.model.entity.RewardRecord
*/
public interface RewardRecordMapper extends BaseMapper<RewardRecord> {


    List<RewardRecord> judgeTodayHasAdd(@Param("userId") long userId, @Param("now") LocalDateTime now);
}




