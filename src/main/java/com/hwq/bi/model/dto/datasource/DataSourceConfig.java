package com.hwq.bi.model.dto.datasource;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/5/22 22:19
 * @description
 */
@Data
public class DataSourceConfig {
    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */

    private String description;
    /**
     * 类型
     */
    private String type;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private String port;

    /**
     * 数据库名称
     */
    private String dataBaseName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据源初始连接数
     */
    private Integer initConNum;

    /**
     * 最大连接数
     */
    private Integer maxConNum;

    /**
     * 超时时间
     */
    private Integer timeoutSecond;
}
