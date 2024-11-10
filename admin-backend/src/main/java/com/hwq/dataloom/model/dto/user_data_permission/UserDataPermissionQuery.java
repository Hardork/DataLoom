package com.hwq.dataloom.model.dto.user_data_permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/10
 * @Description:
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDataPermissionQuery {

    private Long userId;
    private Long dataId;
}
