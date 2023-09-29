package com.hwq.bi.model.dto.ai;

import lombok.Data;

/**
 * @Author:HWQ
 * @DateTime:2023/9/27 0:29
 * @Description:
 **/
@Data
public class SetAssistantRequest {
    private String assistantName;
    private String type;
    private boolean historyTalk;
    private String functionDes;
    private String inputModel;
    private String roleDesign;
    private String targetWork;
    private String requirement;
    private String style;
    private String otherRequire;
}
