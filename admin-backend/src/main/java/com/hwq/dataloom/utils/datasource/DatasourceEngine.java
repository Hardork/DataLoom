package com.hwq.dataloom.utils.datasource;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
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
import java.util.Arrays;
import java.util.List;
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
     * 执行create建表语句
     * @param datasourceId 数据源id
     * @param tableName 表名
     * @param tableFields 字段信息
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

    @SneakyThrows
    public void exeDropTable(Long datasourceId, String tableName) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        String dropTableSql = dropTable(tableName);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(dropTableSql)) {
            // Execute the query or update
            preparedStatement.executeUpdate();
        }
    }




    private static final String creatTableSql =
            "CREATE TABLE IF NOT EXISTS `TABLE_NAME`" +
                    "Column_Fields;";

    public String dropTable(String name) {
        return "DROP TABLE IF EXISTS `" + name + "`";
    }

    public String dropView(String name) {
        return "DROP VIEW IF EXISTS `" + name + "`";
    }


    /**
     * 创建insert语句
     * @param name 表名
     * @param dataList 所有行数据
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
     *
     * @param tableName
     * @param tableFields
     * @return
     */
    public String createTableSql(String tableName, List<CoreDatasetTableField> tableFields) {
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
        StringBuilder Column_Fields = new StringBuilder("`");
        for (CoreDatasetTableField tableField : tableFields) {
            Column_Fields.append(tableField.getOriginName()).append("` ");
            TableFieldTypeEnum tableFieldTypeEnum = TableFieldTypeEnum.getEnumByValue(tableField.getType());
            if (tableFieldTypeEnum == TableFieldTypeEnum.TEXT) {
                Column_Fields.append("longtext").append(",`");
                continue;
            }
            if (tableFieldTypeEnum == TableFieldTypeEnum.DATETIME) {
                Column_Fields.append("datetime").append(",`");
                continue;
            }
            if (tableFieldTypeEnum == TableFieldTypeEnum.BIGINT) {
                Column_Fields.append("bigint(20)").append(",`");
                continue;
            }
            Column_Fields.append("longtext").append(",`");
        }

        Column_Fields = new StringBuilder(Column_Fields.substring(0, Column_Fields.length() - 2));
        Column_Fields = new StringBuilder("(" + Column_Fields + ")\n");
        return Column_Fields.toString();
    }

}
