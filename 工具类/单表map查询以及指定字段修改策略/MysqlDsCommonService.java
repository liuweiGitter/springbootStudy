package com.telecom.js.noc.hxtnms.operationplan.service.common;

import com.telecom.js.noc.hxtnms.operationplan.mapper.mysql.MysqlDsCommonMapper;
import com.telecom.js.noc.hxtnms.operationplan.utils.ResponseBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-07-08 10:52
 * Desc: Mysql数据源的公共服务
 */
@Service
public class MysqlDsCommonService {

    @Autowired
    private MysqlDsCommonMapper mysqlDsCommonMapper;

    //修改操作
    public int update(String sql){
        return mysqlDsCommonMapper.update(getMap(sql));
    }
    //复合操作，mybatis支持在一个增删改标签中执行多条不同类型的sql语句，分号分隔即可
    public int complexOperation(String sql){
        return mysqlDsCommonMapper.complexOperation(getMap(sql));
    }
    //map结果集查询
    public Map<String,Object> resultMapQuery(String sql){
        return mysqlDsCommonMapper.resultMapQuery(getMap(sql));
    }

    private Map<String,String> getMap(String sql){
        Map<String,String> sqlMap = new HashMap<>();
        sqlMap.put("sql",sql);
        return sqlMap;
    }

    //新增、修改时唯一索引检查是否存在
    public ResponseBox ifExistOne(String tableName, Map<String,Object> paramMap, String indexConflictMsg){
        if(mysqlDsCommonMapper.ifExistOne(tableName,paramMap)){
            return new ResponseBox("唯一索引冲突！字段["+indexConflictMsg+"]");
        }else{
            return null;
        }
    }

    //唯一索引删除时改名
    public int deleteWithUniqueIndex(String tableName, String uniqueColumn, List idList){
        int resultCount = 0;
        int listLength = idList.size();
        int batchSize = 1000;
        //循环批量
        int circleSize = listLength/batchSize;
        //是否有余量
        boolean remain = listLength % batchSize != 0;
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            resultCount+=mysqlDsCommonMapper.deleteWithUniqueIndex(tableName,uniqueColumn,idList.subList((i - 1) * batchSize, i * batchSize));
        }
        //余量
        if (remain) {
            resultCount+=mysqlDsCommonMapper.deleteWithUniqueIndex(tableName,uniqueColumn,idList.subList(listLength - listLength % batchSize, listLength));
        }
        return resultCount;
    }
}
