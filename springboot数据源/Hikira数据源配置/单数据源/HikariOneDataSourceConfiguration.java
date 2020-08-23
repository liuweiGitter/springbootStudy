package com.ping.job.cover.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * @author liuwei
 * @date 2020-08-20 17:21
 * @desc hikari单数据源注册bean
 */
@Slf4j
@EnableTransactionManagement
@Configuration
public class HikariOneDataSourceConfiguration {

    /**
     * 注册hikari数据源bean
     *
     * @return
     */
    @Bean(name = "hikariDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    @Primary
    public HikariDataSource hikariDataSource() {
        return new HikariDataSource();
    }

    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("hikariDataSource") DataSource dataSource) {
        return SqlSessionFactoryHelper.getFactory(dataSource, "hikari",
                "classpath:/mapper/*Mapper.xml", "com.ping.job.cover.vo");
    }

    @Bean("sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
