package com.hwq.dataloom.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/9/15 20:46
 * @description 数值列数据
 */
@Data
@AllArgsConstructor
@Builder
public class SeriesData {
    private String title;

    private List<Integer> data;
}
