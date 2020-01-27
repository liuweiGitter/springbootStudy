package junit;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import com.telecom.js.noc.hxtnms.operationplan.configure.MysqlDataSourceConfig;
import com.telecom.js.noc.hxtnms.operationplan.configure.NosqlDbConfigure;
import com.telecom.js.noc.hxtnms.operationplan.controller.TestController;
import com.telecom.js.noc.hxtnms.operationplan.entity.SomeEntity;
import com.telecom.js.noc.hxtnms.operationplan.service.impl.ThresholdService;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author liuwei
 * @date 2020-01-27 19:51
 * @desc 三方运行器测试---spring框架运行器：以springboot为例
 * 当类被@RunWith注释修饰, 或者类继承了一个被该注解类修饰的类,
 * JUnit将会使用这个注解所指明的运行器(runner)--而不是JUnit默认的运行器--来运行测试
 *
 * @RunWith 的参数是一个Runner类，通常是org.junit.runners.ParentRunner的子类
 * 如常见的JUnit4、Parameterized、SpringJUnit4ClassRunner
 * 其中，JUnit4是默认运行器，Parameterized是参数化测试运行器，SpringJUnit4ClassRunner则是spring框架运行器
 *
 * @Slf4j 用于日志打印，选注
 * @SpringBootTest 用于指定测试为spring boot风格(配置文件名称和位置按照默认约定)，对于spring boot项目，必注
 *      默认为properties = "application.properties"，不可指定新位置
 *      如需其它配置，配置文件中spring.profiles.active属性指定即可
 * @RunWith 用于指定测试框架运行器，必注
 * @ContextConfiguration 用于注入spring上下文，即spring bean配置文件或配置类，可注入多个配置类，若需要注入自定义bean，必注
 *      如果测试需要依赖数据源，则需要指定redis、mongo、mysql、oracle等数据源配置类
 *      如果测试服务层，则还需要指定服务层类
 *      如果测试控制层，则还需要注入WebApplicationContext对象(不推荐)，并据此创建MockMvc进行测试
 *          推荐直接指定控制类创建MockMvc进行测试
 */
@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NosqlDbConfigure.class, ThresholdService.class, MysqlDataSourceConfig.class})
public class SpringJUnit4ClassRunnerTest {

    /**
     * redis注入：依赖NosqlDbConfigure.class
     */
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 服务层注入：依赖ThresholdService.class和MysqlDataSourceConfig.class
     * 以自定义多数据源配置类为例，需要依赖MysqlDataSourceConfig.class
     * 如果使用druid数据源配置，则可能因为下列依赖错误导致无法启动
     *      No qualifying bean of type 'org.springframework.boot.autoconfigure.jdbc.DataSourceProperties' available
     *      此时需要注释掉@ContextConfiguration注解的值(保留注解本身)
     *          一些情况下，这将导致整个spring boot项目启动，并进行@Test测试
     *          另一些情况下，这将导致所有的上下文注入得不到引用，从而启动失败
     *
     */
    @Autowired
    private ThresholdService thresholdService;

    /**
     * spring上下文注入：不推荐
     */
    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * redis测试
     */
    @Test
    public void testRedis(){
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String redisKey = "liuwei";
        Object value = operations.get(redisKey);
        log.info(JSONObject.toJSONString(value));
    }

    /**
     * 服务层测试
     */
    @Test
    public void testService(){
        List<Integer> list = thresholdService.queryDistinctCity();
        log.info(JSONObject.toJSONString(list));
    }

    /**
     * 控制层测试
     */
    MockMvc mockMvc;

    //初始化mockMvc
    @Before
    public void before() {
        //该对象可以对所有controller层进行测试：但实际上很多时候找不到控制类，不推荐
        //mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        //该对象可以对指定一个或多个controller层进行测试
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
    }

    @Test
    public void testController() throws Exception {
        //请求路径：post请求
        String path = "/api/postTest";
        //入参
        SomeEntity requestBody = new SomeEntity();
        requestBody.setName("liuwei");
        //发起请求
        String content = post(path,requestBody);
        log.info(">>>回参："+content);

        //get请求
        String path2 = "/api/getTest1/";
        String name = "liuwei";
        String content2 = get(path2+name) ;
        log.info(">>>回参："+content2);

        /**
         * 入参或者更多数据格式，参见MediaType类
         * 一些复杂的请求，比如需要经过过滤器和拦截器等，虽可以但不建议在测试类中测试
         */
    }

    //get请求：回参"application/json"格式字符串
    private String get(String path) throws Exception {
        return requestAndGetResult(MockMvcRequestBuilders.get(path));
    }

    //post请求：入参回参都是"application/json"格式字符串
    private String post(String path, Object requestBody) throws Exception {
        MockHttpServletRequestBuilder builders = MockMvcRequestBuilders.post(path);
        String jsonRequest = JSONObject.toJSONString(requestBody);
        //定义入参数据格式
        builders.content(jsonRequest).contentType(MediaType.APPLICATION_JSON);
        return requestAndGetResult(builders);
    }

    private String requestAndGetResult(MockHttpServletRequestBuilder builders) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                //定义允许的出参数据格式，缺省默认接受全部格式
                builders.accept(MediaType.APPLICATION_JSON)
            )
            //打印整个请求流程信息
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        return response.getContentAsString();
    }
}
