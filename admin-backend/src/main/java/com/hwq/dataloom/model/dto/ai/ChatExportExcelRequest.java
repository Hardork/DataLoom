package com.hwq.dataloom.model.dto.ai;

import lombok.Data;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/7
 * @Description: excel导出请求参数
 **/
@Data
public class ChatExportExcelRequest {

    /**
     * 当前对话Id
     */
    private Long chatHistoryId;

    /**
     * 导出数据总数
     */
    private Integer exportCount;

}
