package com.ping.job.cover.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * @author liuwei
 * @date 2020-08-20 17:21
 * @desc hikari多数据源注册--副数据源
 */
@Slf4j
@EnableTransactionManagement
@Configuration
public class HikariSecondDataSourceConfig {

    /**
     * 注册hikari数据源bean
     *
     * @return
     */
    @Bean(name = "hikariDataSourceSecond")
    @ConfigurationProperties(prefix = "spring.datasource.biz-oracle")
    public HikariDataSource hikariDataSource() {
        return new HikariDataSource();
    }

    @Bean("sqlSessionFactorySecond")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("hikariDataSourceSecond") DataSource dataSource) {
        return SqlSessionFactoryHelper.getFactory(dataSource, "biz-oracle",
                "classpath:/mapper/oracle/*Mapper.xml", "com.ping.job.cover.vo");
    }

    @Bean("sqlSessionTemplateSecond")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactorySecond") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
