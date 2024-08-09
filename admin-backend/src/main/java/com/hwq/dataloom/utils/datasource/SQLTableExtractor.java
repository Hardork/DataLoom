package com.hwq.dataloom.utils.datasource;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;

public class SQLTableExtractor {

    public static List<String> getTableNames(String sql) throws Exception {
        List<String> tableNames = new ArrayList<>();

        // 解析SQL语句
        Statement statement = CCJSqlParserUtil.parse(sql);

        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            SelectBody selectBody = selectStatement.getSelectBody();

            // 检查是否是联合查询
            if (selectBody instanceof SetOperationList) {
                List<SelectBody> selects = ((SetOperationList) selectBody).getSelects();
                for (SelectBody body : selects) {
                    tableNames.addAll(getTableNamesFromSelectBody(body));
                }
            } else {
                tableNames.addAll(getTableNamesFromSelectBody(selectBody));
            }
        }

        return tableNames;
    }

    private static List<String> getTableNamesFromSelectBody(SelectBody selectBody) {
        List<String> tableNames = new ArrayList<>();

        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            FromItem fromItem = plainSelect.getFromItem();
            tableNames.add(fromItem.toString());

            // 处理JOIN语句
            List<Join> joins = plainSelect.getJoins();
            if (joins != null) {
                for (Join join : joins) {
                    tableNames.add(join.getRightItem().toString());
                }
            }
        }

        return tableNames;
    }

    public static void main(String[] args) {
       String sqlQuery =
           "SELECT a.id, b.name\n" +
           "FROM users\n" +
           "JOIN orders b ON a.id = b.user_id\n" +
           "WHERE a.age > 30\n" +
           "UNION\n" +
           "SELECT c.id, d.name\n" +
           "FROM customers c\n" +
           "JOIN purchases d ON c.id = d.customer_id;";
        try {
            List<String> tableNames = getTableNames(sqlQuery);
            System.out.println("涉及到的表名：" + tableNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
