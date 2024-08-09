package com.hwq.dataloom.model.dto.user_data;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/4/28 19:27
 * @description
 */
@Data
public class ShareUserDataRequest {
    /**
     * 数据集id
     */
    private Long id;

    /**
     * 数据权限
     */
    private Integer permission;
}
