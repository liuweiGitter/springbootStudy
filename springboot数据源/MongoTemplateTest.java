package harpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.telecom.js.noc.hxtnms.operationplan.entity.OperplanPerformance;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: liuwei
 * Date: 2019-04-29 10:14
 * Desc: mongoTemplate测试类，测试其各种常用接口方法
 */

@Slf4j
public class MongoTemplateTest {

    private MongoTemplate mongoTemplate;
    //private String host_cn2 = "马赛克";
    private String host = "马赛克";
    private int port = 马赛克;
    private String databaseName = "马赛克";
    private String collectionName = "马赛克";
    private Class<?> entityClass = OperplanPerformance.class;

    //获取mongoTemplate对象
    //测试时很难避免insert插入时去除_class字段，忽略此字段
    //springboot注入bean时则需要配置MongoDB bean注入内容，去除该字段
    private void getMongoTemplate(){
        MongoClient mongoClient = new MongoClient( host, port);
        mongoTemplate = new MongoTemplate(mongoClient,databaseName);
        log.info("Get mongoTemplate successfully");
    }

    //查询条件示例
    private void queryConditionDemo(){
        //查询条件，连缀拼接
        Query query = new Query();
        Criteria criteria = new Criteria();
        //拼接示例：
        criteria.where("列名").is("=").ne("!=").lt("<").lte("<=").gt(">").gte(">=");
        criteria.where("列名").in("in","a","list").in(new ArrayList<String>()).nin("not in","a","list").nin(new ArrayList<String>());
        criteria.where("列名").not();//非null
        criteria.where("列名1").and("列名2").and("列名3");//多列and条件
        criteria.where("列名1").andOperator(Criteria.where("列名2"),Criteria.where("列名3"));//多列and条件
        criteria.where("列名1").orOperator(Criteria.where("列名2"),Criteria.where("列名3"));//多列or条件
        criteria.where("列名1").norOperator(Criteria.where("列名2"),Criteria.where("列名3"));//多列nor条件
        //between条件双拼，模糊条件regrex，null条件未知

        query.addCriteria(criteria);
        query.cursorBatchSize(1000);//批量查询
        query.with(new Sort(Sort.Direction.ASC,"列名1","列名2"));//排序
        //query.with(Pageable pageable);分页查询
    }

    //查询方法示例
    private void queryMethodDemo(){
        //查询所有字段的query
        //Query query = new Query();
        //查询指定字段的query，默认_id会查询，可设为false
        Document queryObject = new Document();
        Document fieldsObject = new Document();
        fieldsObject.put("_id", false);//不查询_id列
        fieldsObject.put("列名1", true);
        fieldsObject.put("列名2", true);
        Query query = new BasicQuery(queryObject,fieldsObject);
        //条件查询一条数据
        mongoTemplate.findOne(query,entityClass);
        mongoTemplate.findOne(query,entityClass,collectionName);
        //查询多条数据
        mongoTemplate.find(query,entityClass);
        mongoTemplate.find(query,entityClass,collectionName);
        //查询所有数据
        mongoTemplate.findAll(entityClass);
        mongoTemplate.findAll(entityClass,collectionName);
        //通过id查询
        mongoTemplate.findById("id",entityClass);
        mongoTemplate.findById("id",entityClass,collectionName);
        //去重查询
        mongoTemplate.findDistinct(query,"去重字段",entityClass,entityClass);
        mongoTemplate.findDistinct(query,"去重字段",collectionName,entityClass,entityClass);
        // 其它很多接口，不赘述
    }

    //MongoDB查询数据测试
    @Test
    public void queryTest(){
        getMongoTemplate();

        //查询指定字段的query
        Document queryObject = new Document();
        Document fieldsObject = new Document();
        fieldsObject.put("emsId", true);
        fieldsObject.put("deviceId", true);
        Query query = new BasicQuery(queryObject,fieldsObject);

        //查询条件拼接
        query.addCriteria(Criteria.where("emsId").is("23094FE5B0124D09BC0D92A4BFB4EA41"));

        List<OperplanPerformance> list = mongoTemplate.find(query, OperplanPerformance.class,collectionName);
        for (int i = 0;i<list.size();i++){
            log.info(JSON.toJSONString(list.get(i)));
        }
    }

    //MongoDB修改数据测试
    @Test
    public void updateTest(){
        getMongoTemplate();
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        //查询条件，连缀拼接
        Query query = new Query();
        Criteria criteria = Criteria.where("emsId").is("23094FE5B0124D09BC0D92A4BFB4EA41");
        query.addCriteria(criteria);

        //修改内容，连缀拼接
        Update update = new Update();
        update.set("planId","后台修改数据").set("pfItem","性能x");
        //update.currentTimestamp("update_time");//修改当前时间
        update.currentDate("updateTime");//修改当前日期，格式如2019-04-29 08:38:26.304
        //update.min("列名","如果小于此值就修改").max("列名","如果大于此值就修改");

        //【修改数据】
        UpdateResult result = null;
        //修改第一条
        //result = mongoTemplate.updateFirst(query,update,entityClass);
        //result = mongoTemplate.updateFirst(query,update,collectionName);
        //修改多条
        //result = mongoTemplate.updateMulti(query,update,entityClass);
        result = mongoTemplate.updateMulti(query,update,collectionName);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info(JSONObject.toJSONString(result));
        log.info(result.wasAcknowledged()+"");
    }

    //MongoDB删除数据测试
    @Test
    public void deleteTest(){
        getMongoTemplate();
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        //查询条件，连缀拼接
        Query query = new Query();
        Criteria criteria = Criteria.where("emsId").is("23094FE5B0124D09BC0D92A4BFB4EA41");
        query.addCriteria(criteria);

        //【删除数据】
        DeleteResult result = null;
        //删除对象，通过查询条件
        //result = mongoTemplate.remove(query,collectionName);
        //result = mongoTemplate.remove(query,entityClass);
        //删除一个对象，传入实体类，通过对象id
        //result = mongoTemplate.remove(new OperplanPerformance("5cc6bb3c324c0000c400080a"));
        result = mongoTemplate.remove(new OperplanPerformance("5cc6e240324c0000c4000836"),collectionName);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info(JSONObject.toJSONString(result));
        log.info(result.wasAcknowledged()+"");
    }

    //MongoDB插入数据测试
    @Test
    public void insertTest(){
        getMongoTemplate();
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        //【插入数据】
        OperplanPerformance operplanPerformance = new OperplanPerformance();
        operplanPerformance.setPlanId("后台插入数据");
        operplanPerformance.setEmsId("12334");
        List<OperplanPerformance> list = new ArrayList<OperplanPerformance>();
        list.add(operplanPerformance);
        //插入一个对象
        //mongoTemplate.insert(operplanPerformance);
        mongoTemplate.insert(operplanPerformance,collectionName);
        //插入一组对象
        //mongoTemplate.insert(list,collectionName);
        //mongoTemplate.insert(list,entityClass);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    //MongoDB集合结构测试
    @Test
    public void collectionTest(){
        getMongoTemplate();
        //删除集合
        mongoTemplate.dropCollection(collectionName);
        //创建集合
        mongoTemplate.createCollection(collectionName);
        //获取集合
        mongoTemplate.getCollection(collectionName);
        //判断集合是否存在
        mongoTemplate.collectionExists(collectionName);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    //带鉴权的mongodb库，获取mongoTemplate
    private MongoTemplate getAuthenMongoTemplate(){
        String host = "132.60.14.94";
        int port = 38010;
        //注：鉴权用的库和实际连接的库可能不是一个库
        String database4Authen = "admin";
        String database = "otms_0250";
        String username = "root";
        String password = "Otms_otms";

        //服务地址
        ServerAddress serverAddress = new ServerAddress(host,port);
        //鉴权
        MongoCredential credential = MongoCredential.createCredential(username,database4Authen,password.toCharArray());
        //连接选项(最大连接数，超时时间等，使用默认即可)
        MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder().build();

        MongoClient mongoClient = new MongoClient(serverAddress,credential,mongoClientOptions);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,database);
        log.info("Get Otms mongoTemplate successfully");
        return mongoTemplate;
    }

    @Test
    public void testAuthMongo(){
        MongoTemplate mongoTemplate = getAuthenMongoTemplate();
        String collectionName = "T_HISTORY_PERFORMANCE";
        Document queryObject = new Document();
        Document fieldsObject = new Document();
        fieldsObject.put("_id", false);
        fieldsObject.put("value", true);
        Query query = new BasicQuery(queryObject,fieldsObject);
        Criteria criteria = Criteria.where("port_id").is("60AF72064CA730B1A0BF42DECCA2037B");
        query.addCriteria(criteria);
        List<String> list = mongoTemplate.find(query,String.class,collectionName);
        log.info(JSONObject.toJSONString(list));

    }
}
