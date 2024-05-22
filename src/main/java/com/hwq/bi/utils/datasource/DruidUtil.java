package com.hwq.bi.utils.datasource;/**
 * @author HWQ
 * @date 2024/4/22 21:27
 * @description
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.model.dto.datasource.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据库连接池
 */
@Slf4j
public class DruidUtil {

    public static DruidDataSource dataSource = new DruidDataSource();

    static {
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setTestOnBorrow(true);
    }


    /**
     * 校验连接
     * @param dataSourceConfig
     * @return
     * @throws SQLException
     */
    public static boolean checkConnectValid(DataSourceConfig dataSourceConfig) throws SQLException {
        String host = dataSourceConfig.getHost();
        String port = dataSourceConfig.getPort();
        String dataBaseName = dataSourceConfig.getDataBaseName();
        String userName = dataSourceConfig.getUserName();
        String password = dataSourceConfig.getPassword();
        // 构造URL
        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://" )
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(dataBaseName);
        Connection conn = null;
        try {
           conn = DriverManager.getConnection(url.toString(), userName, password);
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库连接失败");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return true;
    }

    /**
     * 获取数据库的表名
     * @param dataSourceConfig
     * @return
     */
    public static List<String> getSchemas(DataSourceConfig dataSourceConfig) {
        String host = dataSourceConfig.getHost();
        String port = dataSourceConfig.getPort();
        String dataBaseName = dataSourceConfig.getDataBaseName();
        String userName = dataSourceConfig.getUserName();
        String password = dataSourceConfig.getPassword();
        // 构造URL
        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://" )
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(dataBaseName);
        List<String> tables = new ArrayList<>();
        try {
            // 通过 DruidDataSourceFactory 创建连接池
            // 其他配置项...
            Connection conn = DriverManager.getConnection(url.toString(), userName, password);

            // 获取数据库连接
            try (
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        } finally {
            // 关闭连接池
            if (dataSource != null) {
                dataSource.close();
            }
        }
        return tables;
    }

    public static void main(String[] args) throws SQLException {
//        DruidUtil.getSchemas("localhost", "3306", "bi", "root", "hwq2003121");
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setHost("47.98.240.155");
        dataSourceConfig.setPort("3307");
        dataSourceConfig.setDataBaseName("bi");
        dataSourceConfig.setUserName("root");
        dataSourceConfig.setPassword("hwq2003121");
        DruidUtil.checkConnectValid(dataSourceConfig);
        dataSourceConfig.setPort("3306");
        System.out.println(DruidUtil.getSchemas(dataSourceConfig));


    }

}