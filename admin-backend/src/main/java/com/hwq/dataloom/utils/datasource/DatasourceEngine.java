package com.hwq.dataloom.utils.datasource;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.enums.TableFieldTypeEnum;
import com.hwq.dataloom.model.vo.dashboard.GetChartDataVO;
import com.hwq.dataloom.model.vo.dashboard.SeriesData;
import com.hwq.dataloom.model.vo.dashboard.XArrayData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @author HWQ
 * @date 2024/8/23 00:58
 * @description 数据源引擎 - 获取数据源数据
 */
@Service
@Slf4j
public class DatasourceEngine {

    @Resource
    private Map<Integer, DataSource> dataSourceMap;

    @Value("${datasource.page.size:10}")
    private int pageSize; // 查询一页的记录数量

    /**
     * 执行查询数据源SQL
     *
     * @param datasourceId 数据源id
     * @param sql          执行SQL
     * @param parameters   SQL占位符参数
     * @return 结果集
     */
    @SneakyThrows
    public ResultSet execSelectSql(Long datasourceId, String sql, Object... parameters) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        Connection connection = dataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set parameters to prevent SQL injection
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            // Execute the query or update
            return preparedStatement.executeQuery();
        }
    }

    /**
     * 执行SQL语句并将列集合和记录犯规
     *
     * @param datasourceId 数据源id
     * @param parameters   参数
     * @return 查询结果
     */
    public CustomPage<Map<String, Object>> execSelectSqlToQueryAICustomSQLVO(Long datasourceId, String sql, Integer pageNo, Integer pageSize, Object... parameters) throws SQLException {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        try (Connection connection = dataSource.getConnection()) {
            // 分页查询数据
            return pageQuery(sql, pageNo, pageSize, connection);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询数据
     *
     * @param originalSql 原始查询数据
     * @param pageNo      页下标
     * @param connection  数据库连接
     * @return 分页数据
     * @throws SQLException SQL异常
     */
    public CustomPage<Map<String, Object>> pageQuery(String originalSql, int pageNo, int pageSize, Connection connection) throws SQLException {
        int count = getSelectCount(originalSql, connection);
        ResultSet rs = getData(originalSql, pageNo, pageSize, connection);
        List<Map<String, Object>> res = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        // Execute the query or update
        // 处理查询结果
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(metaData.getColumnName(i));
        }
        while (rs.next()) {
            Map<String, Object> resMap = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                resMap.put(metaData.getColumnName(i), rs.getString(i));
            }
            res.add(resMap);
        }
        return CustomPage.<Map<String, Object>>builder()
                .total(count)
                .current(pageNo)
                .size(pageSize)
                .records(res)
                .sql(originalSql)
                .columns(columns)
                .build();
    }

    public ResultSet getData(String originalSql, int pageNo, int pageSize, Connection connection) throws SQLException {
        String pageQuerySql = getPageQuerySql(originalSql, pageNo, pageSize);
        PreparedStatement preparedStatement = connection.prepareStatement(pageQuerySql);
        return preparedStatement.executeQuery();
    }


    /**
     * 获取查询记录数
     *
     * @param originalSql 查询sql
     * @param connection  数据库连接
     * @return 查询记录数
     * @throws SQLException SQL异常
     */
    public int getSelectCount(String originalSql, Connection connection) throws SQLException {
        try {
            // 获取查询总数的sql
            String countSql = getCountSqlFromOriginalSql(originalSql);
            PreparedStatement preparedStatement = connection.prepareStatement(countSql);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) { // 先调用next()移动游标到第一行
                return rs.getInt("COUNT(*)");
            }
            return 0;
        } catch (SQLException e) {
            log.error("获取查询记录数失败, 执行sql:{}, 失败原因:{}", originalSql, e.getMessage());
            throw e;
        }
    }

    /**
     * 获取统计记录sql
     *
     * @param originalSql 原始sql
     * @return 统计记录sql
     */
    public String getCountSqlFromOriginalSql(String originalSql) {
        return "SELECT COUNT(*) FROM(" + originalSql + ") tmp";
    }


    /**
     * 获取分页查询 Sql
     *
     * @param originalSql 查询sql
     * @param pageNo      查询的页下标
     * @return 获取分页查询的sql
     */
    public String getPageQuerySql(String originalSql, int pageNo, int pageSize) {
        pageNo = (pageNo - 1) * pageSize;
        return "select tmp.* from (" + originalSql + ") as tmp LIMIT " + pageNo + "," + pageSize;
    }


    /**
     * 执行SQL语句获取图表数据
     *
     * @param datasourceId 数据源id
     * @param selectSql    执行SQL
     * @return 图表数据
     */
    public GetChartDataVO execSelectSqlForGetChartDataVO(Long datasourceId, String selectSql) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        // 横轴数据
        XArrayData xArrayData;
        // 纵轴数据
        List<SeriesData> seriesDataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            ResultSet rs = preparedStatement.executeQuery();
            // Execute the query or update
            // 处理查询结果
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 初始化横轴数据
            xArrayData = XArrayData.builder()
                    .title(metaData.getColumnName(1))
                    .values(new ArrayList<>())
                    .build();
            // 初始化纵轴数据
            for (int i = 2; i <= columnCount; i++) {
                SeriesData seriesData = SeriesData.builder()
                        .title(metaData.getColumnName(i))
                        .data(new ArrayList<>())
                        .build();
                seriesDataList.add(seriesData);
            }
            // 遍历ResultSet并逐列提取数据
            while (rs.next()) {
                // 添加纵轴数据
                xArrayData.getValues().add(rs.getString(1));
                // 添加横轴数据
                for (int i = 2; i <= columnCount; i++) {
                    Integer value = rs.getInt(i);
                    seriesDataList.get(i - 2).getData().add(value);  // 按列顺序存入对应的列表
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            log.error("查询数据异常，请检查数据源，查询库：ds_{}，查询SQL:{}", dsIndex, selectSql);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "查询数据异常,请检查数据源");
        }
        return GetChartDataVO.builder()
                .xArrayData(xArrayData)
                .seriesDataList(seriesDataList)
                .build();
    }

    /**
     * 根据id执行更新数据源SQL
     *
     * @param datasourceId 数据源id
     * @param sql          执行SQL
     * @param parameters   参数
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
     *
     * @param datasourceId 数据源id
     * @param name         表名
     * @param dataList     所有行数据
     * @param page         当前插入页
     * @param pageNumber   一页插入数量
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
     *
     * @param datasourceId 数据源ID
     * @param name
     * @param dataList
     * @param page
     * @param pageNumber
     * @param columns
     * @param tableField
     * @return
     */
    @SneakyThrows
    public int execInsertAndUpdate(Long datasourceId, String name, List<String[]> dataList, int page, int pageNumber, String[] columns, TableField tableField) {
        int dsIndex = (int) (datasourceId % (dataSourceMap.size()));
        // 获取对应连接池
        DataSource dataSource = dataSourceMap.get(dsIndex);
        String primaryKey = tableField.getOriginName();
        String insertSql = insertDuplicateSql(name, dataList, page, pageNumber, columns, primaryKey);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            // Execute the query or update
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * 执行create建表语句
     *
     * @param datasourceId 数据源id
     * @param tableName    表名
     * @param tableFields  字段列表
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
     *
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
     *
     * @param name       表名
     * @param dataList   所有行记录
     * @param page       当前插入页
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
     *
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
     * 创建建表语句
     *
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
     *
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
            if (tableField.getIsUnique() != null && tableField.getIsUnique() == 1) {
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
     *
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
