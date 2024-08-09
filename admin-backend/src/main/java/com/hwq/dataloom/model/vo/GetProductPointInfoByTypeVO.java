package com.hwq.dataloom.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 19:18
 * @Description:
 **/
@Data
public class GetProductPointInfoByTypeVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 产品描述
     */
    @TableField(value = "description")
    private String description;


    /**
     * 金额(分) 数据里的100表示100分，即1元
     */
    @TableField(value = "total")
    private Long total;
}
