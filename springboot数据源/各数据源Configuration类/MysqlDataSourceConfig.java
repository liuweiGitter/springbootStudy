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
