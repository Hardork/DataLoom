package com.hwq.dataloom.core.workflow.entitys.edge;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/11/23 17:28
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunCondition {
    /**
     * 运行类型（branch_identify 分支判断运行、condition条件运行）
     */
    private String type;

    private String branchIdentify;

    private List<Condition> conditionList;

    public String getHash() {
        return DigestUtil.sha256Hex(this.toString());
    }
}
