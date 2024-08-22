package com.hwq.dataloom.utils.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/8/22 23:55
 * @description 数据库配置（存储数据源数据）
 */
@Configuration
@ConfigurationProperties(prefix = "datasource-list")
@Data
public class DataSourceProperties {

    private List<DataSourceConfig> sources;

}
@Data
class DataSourceConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}