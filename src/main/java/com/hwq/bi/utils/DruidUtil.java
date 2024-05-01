package com.hwq.bi.utils;/**
 * @author HWQ
 * @date 2024/4/22 21:27
 * @description
 */

//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.Connection;

/**
 * Created by cunxp on 2018/6/6.
 */

public class DruidUtil {

//    private static DruidUtil single = null;
//
//    public static Map<String,DruidDataSource> map = new HashMap<>();
//
//    public DruidUtil() {
//    }
//
//    public DruidUtil(List<DataBase> allDBs) {
//        for (DataBase db:allDBs) {
//            Properties prop = new Properties();
//            if("Mysql".equalsIgnoreCase(db.getType())){
//                prop.setProperty("driver","com.mysql.jdbc.Driver");
//                prop.setProperty("url","jdbc:mysql://"+db.getIpAddress()+":"+db.getPort()+"/"+db.getName());
//                prop.setProperty("connectionProperties","useUnicode=true;characterEncoding=UTF8");
//            }else if ("Oracle".equalsIgnoreCase(db.getType())){
//                prop.setProperty("driver","oracle.jdbc.driver.OracleDriver");
//                prop.setProperty("url","jdbc:oracle:thin:@"+db.getIpAddress()+":"+db.getPort()+":"+db
//                        .getName());
//            }else if("Hive".equalsIgnoreCase(db.getType())){
//                prop.setProperty("driver","org.apache.hive.jdbc.HiveDriver");
//                prop.setProperty("url","jdbc:hive2://"+db.getIpAddress()+":"+db.getPort()+"/"+db.getName());
//            }else {
//                throw new RuntimeException("连接池目前只支持Mysql、Oracle、Hive三种数据库类型！");
//            }
//            prop.setProperty("username",db.getUserName());
//            String passwd = EncryptUtil.aesDecrypt(db.getPasswd());
//            prop.setProperty("password",passwd);
//            prop.setProperty("initialSize","3");
//            prop.setProperty("maxActive","6");
//            prop.setProperty("minIdle","1");
//            prop.setProperty("maxWait","60000");
//            prop.setProperty("filters","stat");
//            prop.setProperty("timeBetweenEvictionRunsMillis","35000");
//            prop.setProperty("minEvictableIdleTimeMillis","30000");
//            prop.setProperty("testWhileIdle","true");
//            prop.setProperty("testOnBorrow","false");
//            prop.setProperty("testOnReturn","false");
//            prop.setProperty("poolPreparedStatements","false");
//            prop.setProperty("maxPoolPreparedStatementPerConnectionSize","200");
//            prop.setProperty("removeAbandoned","true");
//            try {
//                DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory
//                        .createDataSource(prop);
//                map.put(db.getId(),druidDataSource);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("初始化创建连接池失败！");
//            }
//        }
//    }
//
//    /**
//     * 获取实例
//     * @return
//     */
//    public static DruidUtil getInstance(){
//        if (single == null) {
//            synchronized (DruidUtil.class) {
//                if (single == null) {
//                    single = new DruidUtil();
//                }
//            }
//        }
//        return single;
//    }
//
//    public Connection getConnection(String id) throws SQLException {
//        DruidDataSource source = map.get(id);
//        return source.getConnection();
//    }
//
//    public void addDataBaseIn(DataBase db){
//        Properties prop = new Properties();
//        if("Mysql".equalsIgnoreCase(db.getType())){
//            prop.setProperty("driverClassName","com.mysql.jdbc.Driver");
//            prop.setProperty("validationQuery","SELECT 1 FROM DUAL");
//            prop.setProperty("url","jdbc:mysql://"+db.getIpAddress()+":"+db.getPort()+"/"+db.getName());
//            prop.setProperty("connectionProperties","useUnicode=true;characterEncoding=UTF8");
//        }else if ("Oracle".equalsIgnoreCase(db.getType())){
//            prop.setProperty("driverClassName","oracle.jdbc.driver.OracleDriver");
//            prop.setProperty("validationQuery","SELECT 1 FROM DUAL");
//            prop.setProperty("url","jdbc:oracle:thin:@"+db.getIpAddress()+":"+db.getPort()+":"+db
//                    .getName());
//        }else if("Hive".equalsIgnoreCase(db.getType())){
//            prop.setProperty("driverClassName","org.apache.hive.jdbc.HiveDriver");
//            prop.setProperty("validationQuery","SELECT 1");
//            prop.setProperty("url","jdbc:hive2://"+db.getIpAddress()+":"+db.getPort()+"/"+db.getName());
//        }else {
//            throw new RuntimeException("连接池目前只支持Mysql、Oracle、Hive三种数据库类型！");
//        }
//        prop.setProperty("username",db.getUserName());
//        String passwd = EncryptUtil.aesDecrypt(db.getPasswd());
//        prop.setProperty("password",passwd);
//        prop.setProperty("initialSize","3");
//        prop.setProperty("maxActive","10");
//        prop.setProperty("minIdle","3");
//        prop.setProperty("maxWait","60000");
//        prop.setProperty("filters","stat");
//        prop.setProperty("timeBetweenEvictionRunsMillis","35000");
//        prop.setProperty("minEvictableIdleTimeMillis","30000");
//        prop.setProperty("testWhileIdle","true");
//        prop.setProperty("testOnBorrow","false");
//        prop.setProperty("testOnReturn","false");
//        prop.setProperty("poolPreparedStatements","false");
//        prop.setProperty("maxPoolPreparedStatementPerConnectionSize","200");
//        prop.setProperty("removeAbandoned","true");
//        try {
//            DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory
//                    .createDataSource(prop);
//            map.put(db.getId(),druidDataSource);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("新增数据库创建连接池失败！");
//        }
//    }
//
//    public void removeDataBaseOut(DataBase db){
//        DruidDataSource source = map.get(db.getId());
//        source.close();
//        map.remove(db.getId());
//    }
//
//    public boolean containsId(String id){
//        return map.containsKey(id);
//    }
}