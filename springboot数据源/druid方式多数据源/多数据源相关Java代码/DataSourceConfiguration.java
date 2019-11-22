package com.jshx.zq.p2p.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: ZhangXD
 * Description：多数据源注册bean
 */

@Slf4j
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages="com.jshx.zq.p2p.mapper.mysql")
public class DataSourceConfiguration {

    /**
     * 注册druid数据源bean
     *
     * @return
     */
    @Bean(name = "bizMysql")
    @ConfigurationProperties(prefix = "spring.datasource.druid.biz-mysql")
    public DataSource localMysql() {
        return DruidDataSourceBuilder.create().build();
    }

    /*
    更多的数据源bean
    @Bean(name = "sysOracle")
    @ConfigurationProperties(prefix = "spring.datasource.druid.sys_oracle")
    public DataSource cas() {
        return DruidDataSourceBuilder.create().build();
    }*/


    /**
     * 动态数据源配置：多数据源入口
     *
     * @param bizMysql 注入上文定义的数据源bean，此参数可多个
     * @return
     */
    @Bean("multiDataSource")
    @Primary
    public DataSource multiDataSource(@Qualifier("bizMysql") DataSource bizMysql) {
        //多数据源对象
        MultiDataSource multiDataSource = new MultiDataSource();
        //添加数据源map：map中可配置多个数据源
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceEnum.BIZ_MYSQL, bizMysql);
        multiDataSource.setTargetDataSources(dataSourceMap);
        //设置默认数据源
        multiDataSource.setDefaultTargetDataSource(bizMysql);
        return multiDataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("multiDataSource") DataSource multiDataSource) throws IOException {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(multiDataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*Mapper.xml"));
        sqlSessionFactory.setTypeAliasesPackage("com.jshx.zq.p2p.vo");
        SqlSessionFactory bean = null;
        try {
            bean = sqlSessionFactory.getObject();
            log.info(">>>数据源注册成功");
        } catch (Exception e) {
            log.error(">>>数据源注册异常", e);
        }
        return bean;
    }

}
