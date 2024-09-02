package com.hwq.dataloom.service.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/9/1 17:01
 * @description 优惠券任务 Excel 实体，每一行记录对应的实体
 */
@Data
public class CouponTaskExcelEntity {
    @ExcelProperty("用户ID")
    private String userId;

    @ExcelProperty
    private String phone;

    @ExcelProperty
    private String email;
}
