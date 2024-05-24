package com.hwq.bi.model.dto.datasource;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

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
    @NotBlank(message = "数据源名称不得为空")
    @Length(max = 255, message = "数据源长度不得大于255")
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
    @NotEmpty(message = "host不得为空")
    @Length(max = 40, message = "host长度不得大于40位")
    private String host;

    /**
     * 端口号
     */
    @NotEmpty
    @Length(max = 16, message = "port长度不得大于16位")
    private String port;

    /**
     * 数据库名称
     */
    @NotEmpty
    @Length(max = 100, message = "dataBaseName长度不得大于100位")
    private String dataBaseName;

    /**
     * 用户名
     */
    @NotEmpty
    @Length(max = 100, message = "userName长度不得大于255位")
    private String userName;

    /**
     * 密码
     */
    @NotEmpty
    @Length(max = 255, message = "password长度不得大于255位")
    private String password;

    /**
     * 数据源初始连接数
     */
    @Null
    @Max(value = 127, message = "initConNum最大为127")
    @Min(value = 1, message = "initConNum最小为1")
    private Integer initConNum;

    /**
     * 最大连接数
     */
    @Null
    @Max(value = 127, message = "maxConNum最大为127")
    @Min(value = 1, message = "maxConNum最小为1")
    private Integer maxConNum;

    /**
     * 超时时间
     */
    @Null
    @Max(value = 127, message = "timeoutSecond最大为127")
    @Min(value = 0, message = "timeoutSecond最小为0")
    private Integer timeoutSecond;
}
