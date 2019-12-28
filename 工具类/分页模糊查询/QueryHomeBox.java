package com.jshx.zq.p2p.util;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-05-23 15:45
 * Desc: 模糊查询、精确查询、排序、分页 列表综合查询条件全家桶
 */
@Data
public class QueryHomeBox<T> implements Serializable {
    /**
     * 条件使用泛型可以加强参数名称和类型约束
     */
    //模糊条件：实体类，也可以使用relationMap或likeMap，当使用map时，创建QueryHomeBox不需要实体类泛型
    private T like;
    //精确条件：实体类，也可以使用relationMap或equalMap，当使用map时，创建QueryHomeBox不需要实体类泛型
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
    /**
     * 隐藏的鉴权信息
     * 很多数据需要进行用户过滤，用户参数从本地线程池获取
     * 这些参数可能是用户id或用户名或地市等任何查询所需的信息
     * 通常只需要一个参数，多参数时建议放到equalMap中
     */
    private String hiddenInfo;

    //自定义setter：mysql分页limit和oracle分页上下限
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

    //自定义setter：总数查询后赋值总数和总页数
    public void setTotalCount(int totalCount){
        this.totalCount = totalCount;
        this.totalPageNum = totalCount%pageDataCount==0?totalCount/pageDataCount:totalCount/pageDataCount+1;
    }

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

    /**
     * 对于所有参数都可以使用relationMap，但很多数情况下为等值查询
     * 对于所有的等值查询，可以使用数据结构更加简单的equalMap
     */
    private Map<String,Object> equalMap;

    /**
     * 对于所有参数都可以使用relationMap，但很多数情况下为模糊查询
     * 对于所有的模糊查询，可以使用数据结构更加简单的likeMap
     * 半模糊还是全模糊，由实际业务约定
     */
    private Map<String,Object> likeMap;

    //扩展查询：反选、匹配所有关系运算符的操作，少数查询将用到
    //是否反选，默认false
    private boolean inverse;

    //扩展查询：字符串列表，少数查询将用到，如按多个字符串id查询
    private List<String> strList;
    //扩展查询：整数列表，少数查询将用到，如按多个整数id查询
    private List<Integer> intList;

}
