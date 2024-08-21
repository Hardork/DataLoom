package com.hwq.dataloom.utils.datasource;/**
 * @author HWQ
 * @date 2024/4/22 21:27
 * @description
 */

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.model.json.StructDatabaseConfiguration;
import com.hwq.dataloom.model.dto.datasource.PreviewData;
import com.hwq.dataloom.model.dto.datasource.SchemaStructure;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
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
public class MySQLUtil {

    private static final String secretKey = "uUXsN6okXYqsh0BB";

    /**
     * 校验连接
     * @param structDatabaseConfiguration
     * @return
     * @throws SQLException
     */
    public static boolean checkConnectValid(StructDatabaseConfiguration structDatabaseConfiguration) {
        try {
            Connection conn = getConByConfig(structDatabaseConfiguration);
           if (conn != null) {
               conn.close();
           }
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库连接失败");
        }
        return true;
    }

    /**
     * 获取数据库
     * @param structDatabaseConfiguration
     * @param tableName
     * @return
     */
    public static List<SchemaStructure> structure(StructDatabaseConfiguration structDatabaseConfiguration, String tableName) {
        Connection conn = getConByConfig(structDatabaseConfiguration);
        List<SchemaStructure> schemaStructuresList = new ArrayList<>();
        try {
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
     * @param structDatabaseConfiguration
     * @return
     */
    public static List<String> getSchemas(StructDatabaseConfiguration structDatabaseConfiguration) {
        Connection conn = getConByConfig(structDatabaseConfiguration);
        List<String> tables = new ArrayList<>();
        try {
            // 获取数据库连接
            try (
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES")
            ) {
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

    public static PreviewData getPreviewData(StructDatabaseConfiguration structDatabaseConfiguration, String tableName) {
        Connection conn = getConByConfig(structDatabaseConfiguration);
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

            // 打印每行数据,至多显示100行
            int i = 0;
            while (resultSet.next() && i++ < 100) {
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

    /**
     * 获取自定义SQL的结果
     * @param structDatabaseConfiguration
     */
    public static QueryAICustomSQLVO queryCustomSQL(StructDatabaseConfiguration structDatabaseConfiguration, String customSQL) {
        Connection conn = getConByConfig(structDatabaseConfiguration, false);
        QueryAICustomSQLVO queryAICustomSQLVO = new QueryAICustomSQLVO();
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            // 获取数据库连接
            try (
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(customSQL)
            ) {
                // 处理查询结果
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(rsmd.getColumnName(i));
                }
                while (rs.next()) {
                    Map<String, Object> resMap = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        resMap.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                    res.add(resMap);
                }
                conn.close();
            }
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        queryAICustomSQLVO.setColumns(columns);
        queryAICustomSQLVO.setRes(res);
        return queryAICustomSQLVO;
    }

    public static QueryAICustomSQLVO queryCustomSqlWithDefaultCon(String customSQL) {
        StructDatabaseConfiguration structDatabaseConfiguration = new StructDatabaseConfiguration();
        structDatabaseConfiguration.setName("null");
        structDatabaseConfiguration.setDescription("");
        structDatabaseConfiguration.setType("MySQL");
        structDatabaseConfiguration.setHost("localhost");
        structDatabaseConfiguration.setPort("3306");
        structDatabaseConfiguration.setDataBaseName("bi");
        structDatabaseConfiguration.setUserName("root");
        structDatabaseConfiguration.setPassword("hwq2003121");
        return queryCustomSQL(structDatabaseConfiguration, customSQL);
    }

    public static Connection getConByConfig(StructDatabaseConfiguration structDatabaseConfiguration, boolean encrypt) {
        if (structDatabaseConfiguration == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "dataSourceConfig不存在");
        }
        String host = structDatabaseConfiguration.getHost();
        String port = structDatabaseConfiguration.getPort();
        String dataBaseName = structDatabaseConfiguration.getDataBaseName();
        String userName = structDatabaseConfiguration.getUserName();
        String password = structDatabaseConfiguration.getPassword();
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

    public static Connection getConByConfig(StructDatabaseConfiguration structDatabaseConfiguration) {
        if (structDatabaseConfiguration == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "dataSourceConfig不存在");
        }
        String host = structDatabaseConfiguration.getHost();
        String port = structDatabaseConfiguration.getPort();
        String dataBaseName = structDatabaseConfiguration.getDataBaseName();
        String userName = structDatabaseConfiguration.getUserName();
        String password = structDatabaseConfiguration.getPassword();
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

}