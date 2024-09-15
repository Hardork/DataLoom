package com.hwq.dataloom.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/9/15 20:43
 * @description 横轴数据
 */
@Data
@AllArgsConstructor
@Builder
public class XArrayData {
    private String title;

    private List<String> values;
}
