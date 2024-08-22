package com.hwq.dataloom.utils.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/23 00:42
 * @description 数据源连接管理
 */
@Configuration
public class DatasourceConnectionManager {

    @Resource
    private DataSourceProperties dataSourceProperties;

    /**
     * 数据源连接池获取
     * @return 所有的数据库(存储数据源数据)连接池
     */
    @Bean
    public Map<Integer, DataSource> dataSourceMap() {
        Map<Integer, DataSource> dataSourceMap = new HashMap<>();
        List<DataSourceConfig> sources = dataSourceProperties.getSources();
        for (int i = 0; i < sources.size(); i++) {
            DataSourceConfig config = sources.get(i);
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setConnectionTimeout(3000); // 超时时间3s
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setDriverClassName(config.getDriverClassName());
//            hikariConfig.setMaximumPoolSize(); // CPU核心数量的两倍
            hikariConfig.setJdbcUrl(config.getUrl()); // 设置jdbc连接的额外字符串
            hikariConfig.setPoolName("ds_" + i);
            dataSourceMap.put(i, new HikariDataSource(hikariConfig));
        }
        return dataSourceMap;
    }

}
