package com.hwq.bi.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:HWQ
 * @DateTime:2023/10/23 21:11
 * @Description:
 **/
@Data
public class ImageVo implements Serializable {
    private static final long serialVersionUID = -4296258656223039373L;
    private String uid;
    private String name;
    private String status;
    private String url;
}