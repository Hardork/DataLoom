package com.hwq.dataloom.model.vo;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/5/6 11:30
 * @description 数据协作者
 */
@Data
public class DataCollaboratorsVO {
    // 用户信息
    private UserVO userVO;
    // 权限
    private Integer permission;
}
