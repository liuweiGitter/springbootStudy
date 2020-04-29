package util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Author: liuwei
 * Date: 2019-05-08 15:45
 * Desc: redisTemplate工具测试类，在普通测试类中使用
 * redis共7种数据类型，value、list、set、zest、hash、geo、hyperloglog
 * 演示常见的前5种数据类型的存储和读取
 */
@Slf4j
public class RedisTemplateUtil {

    @Data
    static class Student{
        private String id;
        private String name;
        private int age;
        private boolean gender;
        private String className;
        public Student(){

        }
        public Student(String id, String name, int age, boolean gender, String className) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.className = className;
        }
        @Override
        public String toString() {
            return "Student{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", gender=" + gender +
                    ", className='" + className + '\'' +
                    '}';
        }
    }
    private Student student1 = new Student("1","liuwei",29,true,"八年八班");
    private Student student2 = new Student("2","liuwei",29,true,"八年八班");

    private RedisTemplate<String, Object> redisTemplate = getRedisTemplate();

    //获取鉴权的RedisTemplate对象
    public RedisTemplate getRedisTemplate(){
        String host = "127.0.0.1";
        int port = 6379;
        int database = 0;
        String password = "";//本例没有鉴权密码
        //redis单节点配置，集群需配置RedisClusterConfiguration
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(password);
        //连接池参数
        JedisConnectionFactory conn = new JedisConnectionFactory(redisStandaloneConfiguration);
        conn.setTimeout(10000);
        conn.getPoolConfig().setMaxIdle(3);
        conn.getPoolConfig().setMaxTotal(10);
        conn.getPoolConfig().setMaxWaitMillis(1000);
        conn.getPoolConfig().setMinEvictableIdleTimeMillis(30000);
        conn.getPoolConfig().setNumTestsPerEvictionRun(1024);
        conn.getPoolConfig().setTimeBetweenEvictionRunsMillis(30000);
        conn.getPoolConfig().setTestOnBorrow(true);
        conn.getPoolConfig().setTestWhileIdle(true);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(conn);
        redisTemplate.afterPropertiesSet();
        //设置key、value的序列化类型，value设置jackson2JsonRedisSerializer，取值和赋值时value可以是各种数据类型
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        /**
         * 开启事务支持(默认是不支持的，需手动开启)，使得一个方法中的所有redis操作在同一个 Connection 中执行命令
         * 注意，此处仅仅是事务支持，不代表实际开启了事务，同一个 Connection 不代表进行了批命令异常的回滚处理
         * 在开启事务支持后，有3种方法开启实际的事务：
         * 1.对使用redis操作的方法，在操作前添加redisTemplate.multi()，操作后添加redisTemplate.exec()
         *      {
         *      redisTemplate.multi();//开始事务
         *      一系列redis操作...
         *      redisTemplate.exec();//结束事务
         *      }
         * 2.对使用redis操作的方法添加@Transactional注解，操作后添加redisTemplate.exec()
         *       @Transactional //开始事务
         *      {
         *      一系列redis操作...
         *      redisTemplate.exec();//结束事务
         *       }
         * 3.使用redis回调(推荐，首选，会自动开启事务和处理连接的开关，回调中管道效率最高，推荐)
         *      {
                    SessionCallback<Object> callback = new SessionCallback<Object>() {
                        @Override
                        public Object execute(RedisOperations operations) throws DataAccessException {
                            operations.multi();
                            operations.opsForValue().xxx;
                            operations.opsForList().xxx;
                            ...
                            return operations.exec();
                        }
                    };
                    //普通回调
                    redisTemplate.execute(callback);
                    //管道回调
                    redisTemplate.executePipelined(callback);
         *      }
         */
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    //key批量获取和删除、过期、回调、脚本、管道等
    @Test
    public void templateMethod(){
        //key值存在判断
        redisTemplate.hasKey("somekey");
        //模糊匹配查询key集合
        Set<String> studentKeys = redisTemplate.keys("student:*");
        redisTemplate.randomKey();//随机取出一个key
        //删除key或keys
        redisTemplate.delete("student:1");
        redisTemplate.delete(studentKeys);
        //过期时间
        redisTemplate.expire("somekey",10,TimeUnit.HOURS);
        redisTemplate.expireAt("somekey",new Date());
        redisTemplate.getExpire("somekey");
        redisTemplate.getExpire("somekey",TimeUnit.HOURS);
        //重命名key
        redisTemplate.rename("oldKeyName","newKeyName");
        redisTemplate.renameIfAbsent("oldKeyName","如果newKeyName不存在");
        //数据迁移
        redisTemplate.move("移动一个key到另一个db",2);

    }

    //redis回调操作，用于事务处理
    @Test
    public void redisCallback() {
        String name = "session_callback_key";
        String value = "callback_value";
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set(name, value);
                return operations.exec();
            }
        };
        //普通回调
        redisTemplate.execute(callback);
        //管道回调
        redisTemplate.executePipelined(callback);
    }

    //json 字符串操作
    /**
     * String存储时key值策略：
     * java对象存储，通常是对象名:对象id，唯一对象也可以是对象名
     * map存储，通常是map名
     * list存储，通常是对象列表名:对象id
     */
    @Test
    //开启事务，注意，在同一个事务中，set的值不能被立即get到
    @Transactional
    public void stringValue(){
        redisTemplate.multi();
        ValueOperations<String, Object> redis = redisTemplate.opsForValue();
        //set json字符串，无论key是否存在，不存在就新建key-value，存在就覆盖value
        redis.set("num","不过期");
        redis.set("num","10秒过期",10, TimeUnit.SECONDS);
        redis.set("num",456, 3);//从偏移量(下标)处开始覆盖，10秒456
        redis.append("num","_append");//字符串追加，10秒456_append
        redis.getAndSet("num","new_value");//取得旧值，设置新值，10秒456_append
        //set json字符串，key不存在时
        redis.setIfAbsent("setIfAbsent","不过期");
        redis.setIfAbsent("setIfAbsent","10秒过期",10, TimeUnit.SECONDS);
        //set json字符串，key存在时
        redis.setIfPresent("setIfPresent","不过期");
        redis.setIfPresent("setIfPresent","10秒过期",10, TimeUnit.SECONDS);
        //set boolean
        redis.setBit("boolean",0,true);
        redis.setBit("boolean",1,false);
        redis.setBit("boolean",2,false);
        redis.setBit("boolean",3,true);
        //set 数字及增加
        redis.set("Number",3);
        redis.increment("Number");//自增
        redis.decrement("Number");//自减
        redis.increment("Number",-5.2);//加整数或浮点数
        //redis.decrement("Number",3);//减整数，value本身必须是整数，否则减整数也会报错

        //set 一个map
        Map<String,Object> map = new HashMap<>();
        map.put("name","liuwei");
        map.put("age",29);
        redis.set("num",map);//{"@class":"java.util.HashMap","name":"liuwei","age":29}

        /* set 一个java对象
         * object(所有jdk类、自定义类等)的读写：类不必实现序列化，Jackson在存储类对象时会在@class字段存储类全路径，读取解析时会代理反序列化
         * @class字段如果被人为删除或更改，将无法反序列化(即使类自己实现了序列化)
         * 如果类为内部类，必须为静态内部类，且有无参构造
         */
        redis.set("student:"+student1.getId(),student1);
        redis.set("student:"+student2.getId(),student2);

        //set 批量
        redis.multiSet(map);
        redis.multiSetIfAbsent(map);

        //get json字符串：可类型转换为java对象
        redis.get("num");//{"@class":"java.util.HashMap","name":"liuwei","age":29}
        Map<String,Object> mapGet = (Map<String, Object>) redis.get("num");
        log.info("name:{} age:{}",mapGet.get("name"),mapGet.get("age"));
        Student studentGet = (Student) redis.get("student:"+student1.getId());
        log.info(studentGet.toString());//{"@class":"util.RedisTemplateUtil$Student","id":"1","name":"liuwei","age":29,"gender":true,"className":"八年八班"}

        //根据key集合获取多个value的list：key集合可手动指定或模糊查询得到
        Set<String> studentKeys = new HashSet<>();
        studentKeys.add("student:"+student1.getId());
        studentKeys.add("student:"+student2.getId());
        Set<String> studentKeys2 = redisTemplate.keys("student:" + "*");//模糊查询所有student的key
        List<Object> students = redis.multiGet(studentKeys);//list泛型不能强转，在取出Object类型后再强转
        log.info(((Student)students.get(1)).toString());

        //获取子字符串
        redis.get("num",3,-1);//支持负数下标

        //获取boolean
        redis.getBit("boolean",2);

        //get json字符串的长度
        redis.size("num");

        redisTemplate.exec();
    }

    //list操作：只演示操作字符串value，其它数据类型不赘述
    /**
     * list存储时key值策略：
     * java对象或map对象存储，通常是对象列表名:对象id，每一个对象独立存储
     * 也可以是直接对象列表名存储整个list，但只有数据量少且不需要读取特定id的元素时才建议这样存储
     * 每一个对象独立存储时，实际存储的就不是一个list了，而是多个json String，此时，取出所有的list数据参见json 字符串操作
     */
    @Test
    public void listValue(){
        ListOperations<String, Object> redis= redisTemplate.opsForList();
        List<String> dataList = new ArrayList<>();
        dataList.add("data01");
        dataList.add("data02");
        dataList.add("data03");
        //表头表尾插入、弹出，以表头为例，表尾类同
        redis.leftPush("leftPush","list头部插入，若list不存在，创建后插入");
        redis.leftPushAll("leftPushAll","批量插入","最后的在最左",student1);
        redis.leftPushAll("leftPushAll，list必须转为数组才可以插入",dataList.toArray());
        redis.leftPushIfPresent("leftPushIfPresent","key存在时才允许插入");
        redis.leftPop("弹出并移除list的最左边元素，list长度减1");
        redis.leftPop("弹出并移除list的最左边元素，若list无值，等待直到有可弹元素或者超时退出",10,TimeUnit.SECONDS);
        //双表交接队尾队首数据
        redis.rightPopAndLeftPush("弹出本list的right","推到本list的left");
        redis.rightPopAndLeftPush("弹出本list的right，若list无值，等待直到有可弹元素或者超时退出","推到本list的left",10,TimeUnit.SECONDS);
        //其它操作
        redis.size("返回列表的长度，列表不存在时返回0，key值对应非列表时返回错误");
        redis.remove("删除list中从左到右数正数或从右到左数负数个出现的value值",-2,"count为0时删除所有value");
        redis.index("获取list下标处的值，支持负数下标",5);
        redis.range("获取list的子列表，起止下标包含，支持正数下标(0,length-1)和负数下标(-length,-1)，0到-1为全部",0,-1);
        redis.trim("修剪list本体的子列表，对子列表的每一个元素trim",0,-1);
        redis.set("设置list下标处的值",5,"支持负数下标");
    }

    //set操作：只演示操作字符串value，其它数据类型不赘述
    /**
     * set存储时key值策略：
     * 通常是直接set名存储整个set，这样方便set的交并差集运算，除非有特殊的必要的需求，否则，不建议将set元素独立存储(这样也很难保证set的唯一性)
     */
    @Test
    public void setValue(){
        SetOperations<String, Object> redis = redisTemplate.opsForSet();
        //添加value
        redis.add("set1","添加多个value到set",1,1,2);
        redis.add("set2",2,3,4);
        redis.add("set3",3,4,5);
        redis.add("set4",4,5,6);
        //获取value
        redis.pop("随机弹出并删除1个value");
        redis.pop("随机弹出并删除多个value",4);
        redis.randomMember("随机读取一个value");
        redis.randomMembers("随机读取多个(允许重复读取)value",4);
        redis.distinctRandomMembers("随机读取多个不重复的value",4);
        redis.members("获取set的所有元素");
        //多个set的交并差集运算
        Set<String> sets = new HashSet<>();
        sets.add("set2");
        sets.add("set3");
        sets.add("set4");
        redis.intersect("set1","set2");//交集，2
        redis.intersect("set1",sets);//交集，null
        redis.intersectAndStore("set1","set2","set5");//交集，2，写入新集合
        redis.intersectAndStore("set1",sets,"set6");//交集，null，null值不会写入新集合，set6不存在
        redis.union("set1","set2");//并集，"添加多个value到set",1,2,3,4
        redis.union("set1",sets);//并集，"添加多个value到set",1,2,3,4,5,6
        redis.unionAndStore("set1","set2","set7");//并集，"添加多个value到set",1,2,3,4，写入新集合
        redis.unionAndStore("set1",sets,"set8");//并集，"添加多个value到set",1,2,3,4,5,6，写入新集合
        redis.difference("set1","set2");//后者和前者的差值(前者中存在的后者没有的数据)，"添加多个value到set",1
        redis.difference("set1",sets);//后者和前者的差值(前者中存在的后者没有的数据)，"添加多个value到set",1
        redis.differenceAndStore("set1","set2","set9");//后者和前者的差值(前者中存在的后者没有的数据)，"添加多个value到set",1，写入新集合
        redis.differenceAndStore("set1",sets,"set10");//后者和前者的差值(前者中存在的后者没有的数据)，"添加多个value到set",1，写入新集合

        //其它操作
        redis.remove("set","删除set中的多个value",3,6);
        redis.size("返回set的长度");
        redis.isMember("set","判断set中是否有此value");
        redis.move("srcKey","移除一个value到另一个set集合","destKey");
    }

    //zSet操作：只演示操作字符串value，其它数据类型不赘述
    /**
     * zSet存储时key值策略：
     * 通常是直接zSet名存储整个zSet，这样方便zSet的排序以及交并集运算，除非有特殊的必要的需求，否则，不建议将zSet元素独立存储(这样也很难保证zSet的唯一性)
     * 注意zSet是value去重而不是score去重
     */
    @Test
    public void zSetValue(){
        ZSetOperations<String, Object> redis = redisTemplate.opsForZSet();
        //添加(或修改，如果已存在)value
        redis.add("zset","z1",3);
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        ZSetOperations.TypedTuple<Object> tuple2 = new ZSetTypedTuple("a2",3);
        ZSetOperations.TypedTuple<Object> tuple3 = new ZSetTypedTuple("z3",3);
        ZSetOperations.TypedTuple<Object> tuple4 = new ZSetTypedTuple("z2",4);
        tuples.add(tuple2);
        tuples.add(tuple3);
        tuples.add(tuple4);
        redis.add("zset",tuples);
        redis.add("zset1","z1",1);
        redis.add("zset1","z2",2);
        redis.add("zset2","z1",1);
        redis.add("zset2","z3",3);
        redis.add("zset3","z3",3);
        redis.add("zset4","z4",4);
        //分数增减
        redis.incrementScore("zset","z1",3);
        redis.incrementScore("zset","z1",-2);
        //获取value子集或value--score子集：从由小到大的排序set或由大到小的排序set中获取
        //以下仅示例升序set，降序set完全一样，除了方法名中range变为reverseRange
        redis.range("根据排序下标获取zset value的子集",3,5);
        redis.rangeWithScores("根据排序下标获取zset value--score的子集",3,5);
        redis.rangeByScore("根据score区间获取zset value的子集",3,5);
        redis.rangeByScoreWithScores("根据score区间获取zset value--score的子集",3,5);
        redis.rangeByScore("根据score区间和排序下标区间获取zset value的子集",3,5,2,12);
        redis.rangeByScoreWithScores("根据score区间和排序下标区间获取zset value--score的子集",3,5,2,12);
        //多个set的交并集运算
        Set<String> zSets = new HashSet<>();
        zSets.add("zset2");
        zSets.add("zset3");
        zSets.add("zset4");
        redis.intersectAndStore("zset1","zset2","zset5");//交集，非空时写入新集合
        redis.intersectAndStore("zset1",zSets,"zset6");//交集，非空时写入新集合
        redis.unionAndStore("zset1","zset2","zset7");//并集，写入新集合
        redis.unionAndStore("zset1",zSets,"zset8");//并集，写入新集合
        //多个set的聚合加权重的交并集运算：并集类似，不赘述
        RedisZSetCommands.Weights weights = RedisZSetCommands.Weights.of(1,1.3,1.9,2.7);//为每一个zset的score设置权重
        redis.intersectAndStore("zset1",zSets,"zset9", RedisZSetCommands.Aggregate.SUM);//交集，均权，同value score相加，非空时写入新集合
        redis.intersectAndStore("zset1",zSets,"zset9", RedisZSetCommands.Aggregate.MIN);//交集，均权，同value score取最小，非空时写入新集合
        redis.intersectAndStore("zset1",zSets,"zset9", RedisZSetCommands.Aggregate.MAX);//交集，均权，同value score取最大，非空时写入新集合
        redis.intersectAndStore("zset1",zSets,"zset10", RedisZSetCommands.Aggregate.SUM, weights);//交集，自定义权重，同value score相加，非空时写入新集合
        redis.intersectAndStore("zset1",zSets,"zset10", RedisZSetCommands.Aggregate.MIN, weights);//交集，自定义权重，同value score取最小，非空时写入新集合
        redis.intersectAndStore("zset1",zSets,"zset10", RedisZSetCommands.Aggregate.MAX, weights);//交集，自定义权重，同value score取最大，非空时写入新集合

        //其它操作
        redis.remove("zset","删除zset中的多个value",3,6);
        redis.removeRange("根据排序下标批量删除zset中的value",3,5);
        redis.removeRangeByScore("根据score区间批量删除zset中的value",3,5);
        redis.rank("zset","获取value值的分数由小到大排序序号");
        redis.reverseRank("zset","获取value值的分数由大到小排序序号");
        redis.count("获取zset中score区间的value数量",3,5);
        redis.size("获取zset中value数量，或者用zCard方法，没有区别");
        redis.score("获取zset中value的score","value");
    }

    //hash操作：只演示操作字符串value，其它数据类型不赘述
    /**
     * hash存储时key值策略：
     * 通常是直接hash名存储整个hash
     * 注意hash中key是不允许重复的
     */
    @Test
    public void hashValue(){
        HashOperations<String, String, Object> redis = redisTemplate.opsForHash();
        //添加hash
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("hashkey2","hashvalue2");
        hashMap.put("hashkey3","hashvalue3");
        redis.put("添加hashkey--hashvalue","hashkey1","hashvalue1");
        redis.putAll("批量添加hashkey--hashvalue",hashMap);
        redis.putIfAbsent("如果不存在hashkey，添加hashkey--hashvalue","hashkey1","hashvalue1");
        //获取hash
        Set<String> hashKeys = new HashSet<>();
        hashKeys.add("hashkey1");
        hashKeys.add("hashkey2");
        redis.get("获取hash列表中是指定hashkey的hashvalue","hashkey1");
        redis.multiGet("批量获取hash列表中是指定hashkey的hashvalue",hashKeys);
        redis.values("获取所有的hash列表的hashvalue的列表");
        redis.entries("获取所有的hash列表对应的Map<hashkey,hashvalue>列表");
        //其它操作
        redis.delete("批量删除hash列表中的指定hashkey的hash","hashkey1","hashkey2");
        redis.hasKey("判断hash列表中是否存在指定的hashkey","hashkey1");
        redis.keys("获取所有hashkey的集合");
        redis.size("获取所有hashkey的数量");
        redis.increment("对指定hashkey的hashvalue增加一个数值","hashkey1",2.5);
        redis.lengthOfValue("获取指定hashkey的hashvalue的长度","hashkey1");
    }

}

/**
 * zset需要自己实现TypedTuple接口，此接口几乎是必然用到
 */
 class ZSetTypedTuple implements ZSetOperations.TypedTuple{

     private Object value;
     private double score;

    public ZSetTypedTuple() {

    }

    public ZSetTypedTuple(Object value, double score) {
        this.value = value;
        this.score = score;
    }

    @Override
     public Object getValue() {
         return this.value;
     }

     @Override
     public Double getScore() {
         return this.score;
     }

     @Override
     public int compareTo(Object o) {
        if (null != o && o instanceof ZSetOperations.TypedTuple){
            return ((ZSetOperations.TypedTuple)o).getScore()==this.score?1:0;
        }else{
            return 0;
        }
     }
 }
