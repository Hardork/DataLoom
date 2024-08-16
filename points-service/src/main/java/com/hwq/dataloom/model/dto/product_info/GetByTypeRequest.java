package com.hwq.dataloom.model.dto.product_info;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 19:08
 * @Description:
 **/
@Data
public class GetByTypeRequest {
    private Long id;
    private Integer type;
}
