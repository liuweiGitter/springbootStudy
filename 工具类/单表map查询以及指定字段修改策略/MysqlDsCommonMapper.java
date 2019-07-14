package com.telecom.js.noc.hxtnms.operationplan.mapper.mysql;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-07-08 10:27
 * Desc: 本数据源下的公共mapper接口
 * 使用自定义的sql对数据库执行增删改查操作，尤其是对于一些简单、通用的物理删除、简单字段修改和map结果集查询的操作
 * sql参数为任何完整的增删改查sql语句
 * 对于查询操作，主要针对单表单检索条件的Map结果集查询，多表以及复杂查询或者非Map结果集的查询，建议使用独立的mapper.xml
 */
@Repository
public interface MysqlDsCommonMapper {

    /*
    注意：mybatis命名传参时，$取值需要封装一层
    如@Param("sql") String sql，${sql}会解析为sql.getSql()，从而报错
    sql可以封装入@Param("sqlMap") Map<String,String> sqlMap
     */

    //插入操作：较少使用，仅示例说明，不实现
    //int insert(@Param("sqlMap") Map<String,String> sqlMap);

    //(物理)删除操作：较少使用，仅示例说明，不实现
    //int delete(@Param("sqlMap") Map<String,String> sqlMap);

    //修改操作
    int update(@Param("sqlMap") Map<String,String> sqlMap);

    //复合操作，mybatis支持在一个增删改标签中执行多条不同类型的sql语句，分号分隔即可
    int complexOperation(@Param("sqlMap") Map<String,String> sqlMap);

    //map结果集查询
    Map<String,Object> resultMapQuery(@Param("sqlMap") Map<String,String> sqlMap);

    //新增、修改时唯一索引检查是否存在
    boolean ifExistOne(@Param("tableName") String tableName, @Param("paramMap") Map<String,Object> paramMap);

    //逻辑删除时唯一索引列改名
    int deleteWithUniqueIndex(@Param("tableName") String tableName,@Param("uniqueColumn") String uniqueColumn,@Param("idList") List idList);
}
