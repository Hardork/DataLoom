package com.hwq.dataloom.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/23 09:21
 * @description 存储数据源数据库配置
 */
@Configuration
@ConfigurationProperties(prefix = "datasource-list")
@Data
public class DatasourceDatabaseConfig {

    private List<DataSourceConfig> sources;

    /**
     * 数据源连接池获取
     * @return 所有的数据库(存储数据源数据)连接池
     */
    @Bean
    public Map<Integer, DataSource> dataSourceMap() {
        Map<Integer, DataSource> dataSourceMap = new HashMap<>();
        for (int i = 0; i < sources.size(); i++) {
            DataSourceConfig config = sources.get(i);
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setConnectionTimeout(6000); // 超时时间3s
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setMaximumPoolSize(2);
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setDriverClassName(config.getDriverClassName());
            hikariConfig.setJdbcUrl(config.getUrl()); // 设置jdbc连接的额外字符串
            hikariConfig.setPoolName("ds_" + i);
            dataSourceMap.put(i, new HikariDataSource(hikariConfig));
        }
        return dataSourceMap;
    }

    @Data
    static class DataSourceConfig {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }
}
