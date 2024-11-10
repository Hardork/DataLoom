package com.hwq.dataloom.model.dto.user_data;

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
public class UserDataSecret {
    private Integer id;
    private Integer permission;
    private String readSecretKey;
    private String writeSecretKey;
}
