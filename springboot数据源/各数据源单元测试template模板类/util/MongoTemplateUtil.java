package util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Author: liuwei
 * Date: 2019-05-08 15:45
 * Desc: mongoTemplate工具测试类，在普通测试类中使用
 */
@Slf4j
public class MongoTemplateUtil {

    //private String host_cn2 = "马赛克";
    private String host = "马赛克";
    private int port = 马赛克;
    private String databaseName = "马赛克";
    private String username = "马赛克";
    private String password = "马赛克";
    private String database4Authen = "马赛克";

    //获取免鉴权的mongoTemplate对象
    public MongoTemplate getMongoTemplate(){
        MongoClient mongoClient = new MongoClient( host, port);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,databaseName);
        log.info("Get mongoTemplate successfully");
        return  mongoTemplate;
    }

    //获取鉴权的mongoTemplate对象
    public MongoTemplate getAuthenMongoTemplate(){

        //服务地址
        ServerAddress serverAddress = new ServerAddress(host,port);
        //鉴权
        MongoCredential credential = MongoCredential.createCredential(username,database4Authen,password.toCharArray());
        //连接选项(最大连接数，超时时间等，使用默认即可)
        MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder().build();

        MongoClient mongoClient = new MongoClient(serverAddress,credential,mongoClientOptions);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,databaseName);
        log.info("Get mongoTemplate successfully");
        return  mongoTemplate;
    }
}
