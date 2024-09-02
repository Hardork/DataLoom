package com.hwq.dataloom.utils.datasource;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.enums.TableFieldTypeEnum;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

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
     * 执行查询数据源SQL
     * @param datasourceId 数据源id
     * @param sql 执行SQL
     * @param parameters SQL占位符参数
     * @return 结果集
     */
    @SneakyThrows
    public ResultSet execSelectSql(Long datasourceId, String sql, Object... parameters) {
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
     * 根据id执行更新数据源SQL
     * @param datasourceId 数据源id
     * @param sql 执行SQL
     * @param parameters 参数
     * @return 影响行数
     */
    @SneakyThrows
    public int execUpdateSql(Long datasourceId, String sql, Object... parameters) {
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

    /**
     * 执行insert语句
     * @param datasourceId 数据源id
     * @param name 表名
     * @param dataList 所有行数据
     * @param page 当前插入页
     * @param pageNumber 一页插入数量
     * @return 影响行数
     */
    @SneakyThrows
    public int execInsert(Long datasourceId, String name, List<String[]> dataList, int page, int pageNumber) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        String insertSql = insertSql(name, dataList, page, pageNumber);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            // Execute the query or update
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * 执行增量更新insert语句
     * @param datasourceId
     * @param name
     * @param dataList
     * @param page
     * @param pageNumber
     * @param columns
     * @param tableField
     * @return
     */
    @SneakyThrows
    public int execInsertAndUpdate(Long datasourceId, String name, List<String[]> dataList, int page, int pageNumber,String[] columns ,TableField tableField) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        String primaryKey = tableField.getOriginName();
        String insertSql = insertDuplicateSql(name, dataList, page, pageNumber, columns , primaryKey);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            // Execute the query or update
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * 执行create建表语句
     * @param datasourceId 数据源id
     * @param tableName 表名
     * @param tableFields 字段列表
     */
    @SneakyThrows
    public void exeCreateTable(Long datasourceId, String tableName, List<CoreDatasetTableField> tableFields) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        ThrowUtils.throwIf(tableFields.isEmpty(), ErrorCode.PARAMS_ERROR, "字段不得为空");
        String tableSql = createTableSql(tableName, tableFields);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(tableSql)) {
            // Execute the query or update
            preparedStatement.executeUpdate();
        }
    }

    /**
     * 执行drop table语句
     * @param datasourceId
     * @param tableName
     */
    @SneakyThrows
    public void exeDropTable(Long datasourceId, String tableName) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        String dropTableSql = dropTableSql(tableName);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(dropTableSql)) {
            // Execute the query or update
            preparedStatement.executeUpdate();
        }
    }


    private static final String creatTableSql =
            "CREATE TABLE IF NOT EXISTS `TABLE_NAME`" +
                    "Column_Fields;";

    private String dropTableSql(String name) {
        return "DROP TABLE IF EXISTS `" + name + "`";
    }

    private String dropView(String name) {
        return "DROP VIEW IF EXISTS `" + name + "`";
    }


    /**
     * 创建insert语句
     * @param name 表名
     * @param dataList 所有行记录
     * @param page 当前插入页
     * @param pageNumber 一页插入数量
     * @return insert语句S
     */
    public String insertSql(String name, List<String[]> dataList, int page, int pageNumber) {
        String insertSql = "INSERT INTO `TABLE_NAME` VALUES ".replace("TABLE_NAME", name);
        StringBuilder values = new StringBuilder();

        int realSize = Math.min(page * pageNumber, dataList.size());
        // 按页插入数据，避免传输语句大小超过MySQL最大接收上限
        for (String[] strings : dataList.subList((page - 1) * pageNumber, realSize)) {
            String[] strings1 = new String[strings.length];
            for (int i = 0; i < strings.length; i++) {
                if (StringUtils.isEmpty(strings[i])) {
                    strings1[i] = null;
                } else {
                    strings1[i] = strings[i].replace("\\", "\\\\").replace("'", "\\'");
                }
            }
            values.append("('").append(String.join("','", Arrays.asList(strings1)))
                    .append("'),");
        }
        return (insertSql + values.substring(0, values.length() - 1)).replaceAll("'null'", "null");
    }

    /**
     * 创建增量insert语句
     * @param name
     * @param dataList
     * @param page
     * @param pageNumber
     * @param columns
     * @param primaryKey
     * @return
     */
    public String insertDuplicateSql(String name, List<String[]> dataList, int page, int pageNumber, String[] columns, String primaryKey) {
        // 构建INSERT语句开头部分，包含列名
        String insertSql = "INSERT INTO `" + name + "` (" + String.join(",", columns) + ") VALUES ";
        StringBuilder values = new StringBuilder();

        int realSize = Math.min(page * pageNumber, dataList.size());
        // 按页插入数据，避免传输语句大小超过MySQL最大接收上限
        for (String[] strings : dataList.subList((page - 1) * pageNumber, realSize)) {
            String[] escapedValues = new String[strings.length];
            for (int i = 0; i < strings.length; i++) {
                if (StringUtils.isEmpty(strings[i])) {
                    escapedValues[i] = "null";
                } else {
                    escapedValues[i] = strings[i].replace("\\", "\\\\").replace("'", "\\'");
                }
            }
            values.append("('").append(String.join("','", escapedValues)).append("'),");
        }

        // 去掉最后一个逗号
        String finalValues = values.substring(0, values.length() - 1);

        // 构建ON DUPLICATE KEY UPDATE部分
        StringBuilder onDuplicateKeyUpdate = new StringBuilder(" ON DUPLICATE KEY UPDATE ");
        for (String column : columns) {
            if (!column.equals(primaryKey)) { // 排除主键
                onDuplicateKeyUpdate.append(column).append("=VALUES(").append(column).append("), ");
            }
        }
        // 去掉最后一个逗号和空格
        onDuplicateKeyUpdate.setLength(onDuplicateKeyUpdate.length() - 2);

        // 返回完整的SQL语句
        return insertSql + finalValues + onDuplicateKeyUpdate.toString();
    }



    /**
     *  创建建表语句
     * @param tableName
     * @param tableFields
     * @return
     */
    private String createTableSql(String tableName, List<CoreDatasetTableField> tableFields) {
        String dorisTableColumnSql = createTableFieldSql(tableFields);
        return creatTableSql.replace("TABLE_NAME", tableName).replace("Column_Fields", dorisTableColumnSql);
    }


    /**
     * 创建字段部分SQL
     * @param tableFields 表字段信息
     * @return 建表字段语句
     * 示例:
     * age bigint, name text
     * return `age` bigint,`name` longtext
     */
    private String createTableFieldSql(List<CoreDatasetTableField> tableFields) {
        StringBuilder columnFields = new StringBuilder();
        StringBuilder primaryKeyFields = new StringBuilder();
        for (CoreDatasetTableField tableField : tableFields) {
            // 获取字段原始名称并开始拼接字段定义
            columnFields.append("`").append(tableField.getOriginName()).append("` ");
            // 根据字段类型枚举，拼接相应的SQL类型
            TableFieldTypeEnum tableFieldTypeEnum = TableFieldTypeEnum.getEnumByValue(tableField.getType());
            switch (tableFieldTypeEnum) {
                case TEXT:
                    columnFields.append("longtext");
                    break;
                case DATETIME:
                    columnFields.append("datetime");
                    break;
                case BIGINT:
                    columnFields.append("bigint(20)");
                    break;
                default:
                    columnFields.append("longtext"); // 默认类型
                    break;
            }
            // 检查是否为主键
            if (tableField.getIsUnique() == 1) {
                if (primaryKeyFields.length() > 0) {
                    primaryKeyFields.append(", ");
                }
                primaryKeyFields.append("`").append(tableField.getOriginName()).append("`");
            }
            columnFields.append(", "); // 添加逗号和空格
        }
        // 去掉最后多余的逗号和空格
        if (columnFields.length() > 2) {
            columnFields.setLength(columnFields.length() - 2);
        }
        // 如果存在主键字段，添加PRIMARY KEY约束
        if (primaryKeyFields.length() > 0) {
            columnFields.append(", PRIMARY KEY (").append(primaryKeyFields).append(")");
        }
        // 返回完整的字段定义SQL
        return "(" + columnFields.toString() + ")";
    }

    /**
     * ResultSet转List
     * @param resultSet
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> resultSetToMapList(ResultSet resultSet) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                rowMap.put(columnName, columnValue);
            }
            resultList.add(rowMap);
        }
        return resultList;
    }


}
