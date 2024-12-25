package com.hwq.dataloom.model.json.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/9/15 23:28
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupField {
    private String fieldName;
    private String mode;
}
