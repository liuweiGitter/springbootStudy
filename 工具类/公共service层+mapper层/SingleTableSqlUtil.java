package com.telecom.js.noc.hxtnms.operationplan.utils;

import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-07-07 23:05
 * Desc: [单表的指定一个或多个字段修改,单表的(不建议多表)map结果集查询]的唯一检索条件的sql参数工具类
 * 很多情况下，需要修改一张表的一个或少量多个字段，如状态修改、数值修改、时间修改等
 * 因此，控制层、服务层、mapper层、mapper.xml层，每一层都会有大量的重复功能的代码出现
 * 实际上，对于上述情况，对同一个数据源，自服务层及至以下各层，每层只需一个方法即可实现同样的功能
 * 本工具类提供了唯一检索条件的update，若检索条件比较复杂，可自定义sql或者另写mapper方法实现
 *
 * >>>>>>主键列和修改列值为String或数字类型，布尔类型传数字，非数字的所有类型传String
 */
public class SingleTableSqlUtil {

    private SingleTableSqlUtil(){
        throw new IllegalStateException("Utility class allow not to create object !");
    }

    /**
     * >>>>
     * >>>>逻辑删除
     * >>>>
     */

    //逻辑删除：默认，主键列名为id
    public static String logicRemove(String tableName, Object idValue) {
        return logicRemove(tableName,"id", idValue);
    }

    //批量逻辑删除：默认，主键列名为id
    public static String logicRemoveBatch(String tableName, List<? extends Object> idValueList) {
        return logicRemoveBatch(tableName,"id", idValueList);
    }

    //逻辑删除：自定义主键字段
    public static String logicRemove(String tableName, String keyColumn, Object keyValue) {
        return oneColumnUpdate(tableName, keyColumn, keyValue, "del_flag", 1);
    }

    //批量逻辑删除：自定义主键字段
    public static String logicRemoveBatch(String tableName, String keyColumn, List<? extends Object> keyValueList) {
        return oneColumnUpdateBatch(tableName,keyColumn,keyValueList,"del_flag", 1);
    }

    /**
     * >>>>
     * >>>>指定一个字段修改
     * >>>>
     */
    
    //指定一个字段修改：默认，主键列名为id
    public static String oneColumnUpdate(String tableName, Object idValue, String columnName, Object columnValue) {
        return oneColumnUpdate(tableName, "id", idValue, columnName, columnValue);
    }

    //指定一个字段修改：自定义主键字段
    public static String oneColumnUpdate(String tableName, String keyColumn, Object keyValue, String columnName, Object columnValue) {
        return "update "+tableName+" set "+columnName+" = "+getColumnValue(columnValue)+" where "+keyColumn+" = "+getColumnValue(keyValue);
    }

    //指定一个字段批量修改：默认，主键列名为id
    public static String oneColumnUpdateBatch(String tableName, List<? extends Object> idValueList, String columnName, Object columnValue) {
        return oneColumnUpdateBatch(tableName,"id",idValueList,columnName, columnValue);
    }

    //指定一个字段批量修改：自定义主键字段
    public static String oneColumnUpdateBatch(String tableName, String keyColumn, List<? extends Object> keyValueList, String columnName, Object columnValue) {
        StringBuilder sb = new StringBuilder();
        if (keyValueList.get(0) instanceof String){
            sb.append("\""+keyValueList.get(0)+"\"");
            for (int i = 1; i < keyValueList.size(); i++) {
                sb.append(",\""+keyValueList.get(i)+"\"");
            }
        }else{
            sb.append(keyValueList.get(0));
            for (int i = 1; i < keyValueList.size(); i++) {
                sb.append(","+keyValueList.get(i));
            }
        }
        return "update "+tableName+" set "+columnName+" = "+getColumnValue(columnValue)+" where "+keyColumn+" in ("+sb.toString()+")";
    }

    /**
     * >>>>
     * >>>>指定多个字段修改
     * >>>>
     */
    
    //指定多个字段修改：默认，主键列名为id
    public static String multiColumnUpdate(String tableName, Object idValue, Map<String,Object> columnValueMap) {
        return multiColumnUpdate(tableName,"id", idValue, columnValueMap);
    }

    //指定多个字段修改：默认，主键列名为id
    public static String multiColumnUpdate(String tableName, Object idValue, Object[] columnValueArr) {
        return multiColumnUpdate(tableName,"id", idValue, columnValueArr);
    }

    //指定多个字段修改：自定义主键字段
    public static String multiColumnUpdate(String tableName, String keyColumn, Object keyValue, Map<String, Object> columnValueMap) {
        List<String> columnList = new ArrayList<>();
        for (String column : columnValueMap.keySet()) {
            columnList.add(column);
        }
        StringBuilder sbColumns = new StringBuilder(columnList.get(0)+"="+getColumnValue(columnValueMap.get(columnList.get(0))));
        for (int i = 1; i < columnList.size(); i++) {
            sbColumns.append(","+columnList.get(i)+"="+getColumnValue(columnValueMap.get(columnList.get(i))));
        }
        return "update "+tableName+" set "+sbColumns.toString()+" where "+keyColumn+" = "+getColumnValue(keyValue);
    }

    //指定多个字段修改：自定义主键字段
    public static String multiColumnUpdate(String tableName, String keyColumn, Object keyValue, Object[] columnValueArr) {
        StringBuilder sbColumns = new StringBuilder(columnValueArr[0]+"="+getColumnValue(columnValueArr[1]));
        for (int i = 2; i < columnValueArr.length; i+=2) {
            sbColumns.append(","+columnValueArr[i]+"="+getColumnValue(columnValueArr[i+1]));
        }
        return "update "+tableName+" set "+sbColumns.toString()+" where "+keyColumn+" = "+getColumnValue(keyValue);
    }

    /**
     * >>>>
     * >>>>新增
     * >>>>
     */

    //新增
    public static String insertOne(String tableName, Map<String,Object> columnValueMap) {
        StringBuilder sql = new StringBuilder("insert into "+tableName+" (");
        StringBuilder value = new StringBuilder(" values (");
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry:columnValueMap.entrySet()) {
            if (!isFirst){
                sql.append(","+entry.getKey());
                value.append(","+getColumnValue(entry.getValue()));
            }else{
                isFirst = false;
                sql.append(entry.getKey());
                value.append(getColumnValue(entry.getValue()));
            }
        }
        return sql.append(")").append(value).append(")").toString();
    }

    /**
     * >>>>
     * >>>>辅助方法
     * >>>>
     */

    //参数值类型的处理
    private static String getColumnValue(Object value) {
        if (value instanceof String){//字符串类型
            return  "'"+SqlInjectFilter.strFilterAndAddQuotes(value.toString())+"'";
        }else if (value instanceof Number || value instanceof Boolean){//数字类型或布尔类型
            return  ""+value;
        }else{//其他类型必须转为以上3种类型之一，否则抛出异常
            throw new BadSqlGrammarException("SQL statement create with column value of ["+value.toString()+"]",value.toString(),
                    new SQLException("Column value type error ! Type should be or translate to be String,Number or Boolean"));
        }
    }

}
