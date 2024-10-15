package com.hwq.dataloom.account.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwq.dataloom.account.model.entity.MemberAccount;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public interface MemberAccountMapper extends BaseMapper<MemberAccount> {
}
