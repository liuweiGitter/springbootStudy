package com.telecom.js.noc.hxtnms.operationplan.configure;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;

/**
 * Author: liuwei
 * Date: 2019-05-09 11:27
 * Desc: mysql datasource数据源配置
 * JDBC多数据源时手动配置源，绑定该数据源所应用的mapper的package
 * 使用c3p0数据源而非druid
 */
@Slf4j
@Configuration
//相同数据源的mapper，放在同一个package中
@MapperScan(basePackages = "com.telecom.js.noc.hxtnms.operationplan.mapper.mysql", sqlSessionTemplateRef = "sqlSessionTemplateMysql")
public class MysqlDataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "dataSourceMysql")
    @Primary
    public DataSource getDataSource(){
        String driverClass = environment.getProperty("mysql.datasource.driverClassName");
        String jdbcUrl = environment.getProperty("mysql.datasource.url");
        String user = environment.getProperty("mysql.datasource.username");
        String password = environment.getProperty("mysql.datasource.password");
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(driverClass);
        } catch (PropertyVetoException e) {
            log.error("驱动加载异常！\n"+e);
        }
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        //设置c3p0连接池数量和周期时间策略
        //默认值：初始连接数3 最小连接数0 最大连接数15 一次性创建数量3 最大空闲时间0(可以永远空闲而不删除该连接)
        //默认值：连接生存时间0(可以永远存在，如果设为10s，则自创建起10s后删除该连接，如果被占用，占用结束后再删除)
        //默认值：每个连接缓存的PreparedStatement数量0(0不缓存，如果设为10，则会对10条ps语句一起发送db请求，提高请求效率)
        //默认值：获取新连接失败时重复尝试次数30  重复尝试获取新连接时的时间间隔1000ms
        //默认值：获取新连接时等待连接成功的时间0(如果设为10s，则10s之内不能成功获取到连接时，判定此次连接获取失败)
        //默认值：每隔0时间检查一次空闲连接(0不检查，如果设为10s，则每10s检查一次)
        //默认值：通过3个线程实现3个connection被同时执行
        return dataSource;
    }

    @Bean(name = "sqlSessionFactoryMysql")
    @Primary
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSourceMysql") DataSource dataSource){
        //注：为使用mybatis-plus的basemapper，此处工厂bean需改为mybatis工厂
        //SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.telecom.js.noc.hxtnms.operationplan.entity");
        try {
            bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/mysql/*.xml"));
        } catch (IOException e) {
            log.error("mapper路径错误！\n"+e);
        }
        SqlSessionFactory sqlSessionFactory = null;
        try{
            sqlSessionFactory = bean.getObject();
        }catch(Exception e){
            log.error("SqlSessionFactory注册异常！\n"+e);
        }
        return sqlSessionFactory;
    }

    @Bean(name = "dataSourceTransactionManagerMysql")
    @Primary
    public DataSourceTransactionManager getDataSourceTransactionManager(@Qualifier("dataSourceMysql") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplateMysql")
    @Primary
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier("sqlSessionFactoryMysql") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
