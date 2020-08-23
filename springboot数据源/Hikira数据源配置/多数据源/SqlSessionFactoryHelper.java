package com.ping.job.cover.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.spring.boot.starter.SpringBootVFS;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author liuwei
 * @date 2020-08-20 19:46
 * @desc SqlSessionFactory创建辅助类
 */
@Slf4j
public class SqlSessionFactoryHelper {

    /**
     * 获取SqlSessionFactory工厂
     *
     * @param dataSource         数据源
     * @param dataSourceName     数据源名称，用以提示注册信息
     * @param mapperXmlPath      mapper.xml文件路径，格式如"classpath:/mapper/mysql/*Mapper.xml"
     * @param typeAliasesPackage 需要mybatis别名转换的实体类，为类的全路径定义别名为驼峰名称
     * @return
     */
    public static SqlSessionFactory getFactory(DataSource dataSource, String dataSourceName, String mapperXmlPath, String typeAliasesPackage) {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        try {
            sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperXmlPath));
        } catch (IOException e) {
            log.error(">>>路径 {} 不存在", mapperXmlPath, e);
            System.exit(666);
        }
        sqlSessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        //MyBatis无法扫描Spring Boot别名的Bug 添加下面这行代码
        sqlSessionFactory.setVfs(SpringBootVFS.class);
        SqlSessionFactory bean = null;
        try {
            bean = sqlSessionFactory.getObject();
            log.info(">>>数据源{}注册成功", dataSourceName);
        } catch (Exception e) {
            log.error(">>>数据源{}注册异常", dataSourceName, e);
            System.exit(666);
        }
        return bean;
    }
}
