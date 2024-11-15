package com.hwq.dataloom.manager.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
@Data
@Builder
public class UpdateDataBuildMessage {
    private String userName;
    private String updateContent;
}
