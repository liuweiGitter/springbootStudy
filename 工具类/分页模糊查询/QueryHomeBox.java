package com.telecom.js.noc.hxtnms.operationplan.utils;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-05-23 15:45
 * Desc: 模糊查询、精确查询、排序、分页 列表综合查询条件全家桶
 */
@Data
public class QueryHomeBox<T> {
    /**
     * 条件使用泛型以加强参数名称和类型约束
     */
    //模糊条件
    private T like;
    //精确条件
    private T equal;
    //排序：Map<columnName,desc or asc>
    private Map<String,String> order;
    //分页
    private int pageDataCount;//每页数量
    private int queryPageNum;//要查询的页码
    private int totalCount;//总数量
    private int totalPageNum;//总页码
    private int limitStart;//mysql：limit开始下标
    private int rowNumUp;//oracle：rownum上限，<=
    private int rowNumDown;//oracle：rownum下限，>=

    //查询列表
    private List<T> resultList;

    //自定义setter：分页limit和上下限
    public void setQueryPageNum(int queryPageNum){
        this.queryPageNum = queryPageNum;
        if (0 < pageDataCount){
            this.limitStart = (queryPageNum-1)*pageDataCount;
            this.rowNumUp = queryPageNum*pageDataCount;
            this.rowNumDown = 1+(queryPageNum-1)*pageDataCount;
        }
    }
    public void setPageDataCount(int pageDataCount){
        this.pageDataCount = pageDataCount;
        if (0 < queryPageNum){
            this.limitStart = (queryPageNum-1)*pageDataCount;
            this.rowNumUp = queryPageNum*pageDataCount;
            this.rowNumDown = 1+(queryPageNum-1)*pageDataCount;
        }
    }

    //自定义setter：总数查询
    public void setTotalCount(int totalCount){
        this.totalCount = totalCount;
        this.totalPageNum = totalCount%pageDataCount==0?totalCount/pageDataCount:totalCount/pageDataCount+1;
    }

    //扩展查询：反选、匹配所有关系运算符的操作，少数查询将用到
    //是否反选，默认false
    private boolean inverse;
    //匹配所有关系运算符：Map<columnName,Map<relation,notOrColumnValue>>
    //>,>=,<,<=,=,!=,between,not between,like,not like等
    //示例如下：
    /* {
        "列1": { "relation": ">","value": 50 },
        "列2": { "relation": ">=","value": 50 },
        "列3": { "relation": "<","value": 50 },
        "列4": { "relation": "<=","value": 50 },
        "列5": { "relation": "=","value": 50 },
        "列6": { "relation": "<>","value": 50 },
        "列7": { "not":false, "between": 50, "and": 60 },
        "列8": { "not":true, "like": "men" }
    }*/
    private Map<String,Map<String,Object>> relationMap;

}
