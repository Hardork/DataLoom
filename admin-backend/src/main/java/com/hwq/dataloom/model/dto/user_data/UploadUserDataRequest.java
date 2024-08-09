package com.hwq.dataloom.model.dto.user_data;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/4/28 15:13
 * @description
 */
@Data
public class UploadUserDataRequest {
    private String dataName;
    private String description;
    private Boolean publicAll;
}
