package com.hwq.bi.utils.datasource;/**
 * @author HWQ
 * @date 2024/4/22 21:27
 * @description
 */

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.model.dto.datasource.DataSourceConfig;
import com.hwq.bi.model.dto.datasource.PreviewData;
import com.hwq.bi.model.dto.datasource.SchemaStructure;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 连接用户数据库工作类
 */
@Slf4j
public class DruidUtil {

    private static final String secretKey = "uUXsN6okXYqsh0BB";

    /**
     * 校验连接
     * @param dataSourceConfig
     * @return
     * @throws SQLException
     */
    public static boolean checkConnectValid(DataSourceConfig dataSourceConfig) {
        try {
            Connection conn = getConByConfig(dataSourceConfig);
           if (conn != null) {
               conn.close();
           }
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库连接失败");
        }
        return true;
    }

    /**
     * 获取数据库连接
     * @param dataSourceConfig
     * @param tableName
     * @return
     */
    public static List<SchemaStructure> structure(DataSourceConfig dataSourceConfig, String tableName) {
        Connection conn = getConByConfig(dataSourceConfig);
        List<SchemaStructure> schemaStructuresList = new ArrayList<>();
        try {
            // 通过 DruidDataSourceFactory 创建连接池
            // 其他配置项...
            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();
            // 获取指定表的列信息
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            // 获取列名及其类型
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String comment = columns.getString("REMARKS");
                SchemaStructure schemaStructure = new SchemaStructure();
                schemaStructure.setColumnName(columnName);
                schemaStructure.setComment(comment);
                schemaStructure.setType(columnType);
                schemaStructuresList.add(schemaStructure);
            }
            // 关闭结果集和连接
            columns.close();
            conn.close();
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return schemaStructuresList;
    }

    /**
     * 获取数据库的表名
     * @param dataSourceConfig
     * @return
     */
    public static List<String> getSchemas(DataSourceConfig dataSourceConfig) {
        Connection conn = getConByConfig(dataSourceConfig);
        List<String> tables = new ArrayList<>();
        try {
            // 获取数据库连接
            try (
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
                conn.close();
            }
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return tables;
    }

    public static PreviewData getPreviewData(DataSourceConfig dataSourceConfig, String tableName) {
        Connection conn = getConByConfig(dataSourceConfig);
        List<SchemaStructure> schemaStructuresList = new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();
        PreviewData previewData = new PreviewData();
        try {
            // 通过 DruidDataSourceFactory 创建连接池
            // 其他配置项...
            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();
            // 获取指定表的列信息
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            // 获取列名及其类型
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String comment = columns.getString("REMARKS");
                SchemaStructure schemaStructure = new SchemaStructure();
                schemaStructure.setColumnName(columnName);
                schemaStructure.setComment(comment);
                schemaStructure.setType(columnType);
                schemaStructuresList.add(schemaStructure);
            }
            previewData.setField(schemaStructuresList);
            // 查询表数据
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            // 打印每行数据
            while (resultSet.next()) {
                Map<String, String> columnData = new HashMap<>();
                for (SchemaStructure schemaStructure : schemaStructuresList) {
                    String columnName = schemaStructure.getColumnName();
                    String value = resultSet.getString(columnName);
                    columnData.put(columnName, value);
                }
                data.add(columnData);
                System.out.println(columnData);
            }
            previewData.setData(data);
            // 关闭结果集和连接
            columns.close();
            conn.close();
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return previewData;
    }

    public static Connection getConByConfig(DataSourceConfig dataSourceConfig) {
        if (dataSourceConfig == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "dataSourceConfig不存在");
        }
        String host = dataSourceConfig.getHost();
        String port = dataSourceConfig.getPort();
        String dataBaseName = dataSourceConfig.getDataBaseName();
        String userName = dataSourceConfig.getUserName();
        String password = dataSourceConfig.getPassword();
        // 解密
        password = AESUtils.decrypt(password, secretKey);
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
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "连接失败");
        }
        return conn;
    }

//    public static void main(String[] args) throws SQLException {
//        DataSourceConfig dataSourceConfig = new DataSourceConfig();
//        dataSourceConfig.setHost("47.98.240.155");
//        dataSourceConfig.setPort("3307");
//        dataSourceConfig.setDataBaseName("bi");
//        dataSourceConfig.setUserName("root");
//        dataSourceConfig.setPassword("hwq2003121");
//        dataSourceConfig.setPort("3306");
////        DruidUtil.structure(dataSourceConfig, "ai_role");
//        DruidUtil.getPreviewData(dataSourceConfig, "ai_role");
//    }

}