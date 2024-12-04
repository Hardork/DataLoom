package com.hwq.dataloom.utils.datasource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: HWQ
 * @Description: 自定义分页
 * @DateTime: 2024/12/4 10:38
 **/
@Data
@Builder
public class CustomPage<T> implements Serializable {
    private static final long serialVersionUID = 8545996863226528798L;
    private List<T> records;
    private List<String> columns;
    private String sql;
    private long total;
    private long size;
    private long current;
}
