package com.hwq.dataloom.utils.datasource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/23 00:58
 * @description 数据源引擎 - 获取数据源数据
 */
@Service
public class DatasourceEngine {

    @Resource
    private Map<Integer, DataSource> dataSourceMap;

    /**
     * 根据id执行select查询
     */
    @SneakyThrows
    private ResultSet execSelectSql(Long datasourceId, String sql, Object... parameters) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set parameters to prevent SQL injection
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            // Execute the query or update
            return preparedStatement.executeQuery();
        }
    }

    /**
     * 根据id执行update语句
     * @param datasourceId
     * @param sql
     * @param parameters
     * @return
     */
    @SneakyThrows
    private int execUpdateSql(Long datasourceId, String sql, Object... parameters) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set parameters to prevent SQL injection
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            // Process affected rows if needed
            // Execute the query or update
            return preparedStatement.executeUpdate();
        }
    }
}
