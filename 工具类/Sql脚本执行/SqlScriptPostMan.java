package com.telecom.js.noc.hxtnms.operationplan.script.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.telecom.js.noc.hxtnms.operationplan.utils.LocalFileReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author liuwei
 * @date 2019-07-17 15:48
 * @desc sql脚本执行中心，多个数据源的sql脚本执行
 */
@Component
@Slf4j
public class SqlScriptPostMan {
    @Autowired
    private DataSource dataSourceMysql;
    @Autowired
    private DataSource dataSourceOracle;

    private static final String ERROR_LOG_PATH = LocalFileReader.getClassPath() +"logs/sql/error/";

    public enum DataSourceType{
        MYSQL_DB,ORACLE_DB
    }

    /**
     * 执行sql脚本，支持多sql脚本执行，支持指定数据源
     * 使用mybatis ScriptRunner执行sql脚本，sql脚本一行出错会忽略而继续执行下一行，sql脚本一个出错会忽略而继续执行下一个脚本
     * 最终的执行结果：允许存在数据丢失，错误不支持回滚
     * >>>>>>保存错误日志，记录执行错误的sql语句，日志文件如果不存在会自动创建，如果存在，会覆盖写入，即使没有错误抛出也会覆盖写入空字符(文件长度为0)
     * @param dataSourceType 数据源类型
     * @param pathRelativeClassPath sql脚本文件路径，相对classpath路径
     * @return
     */
    public boolean executeScriptsSkipErrors(DataSourceType dataSourceType,String... pathRelativeClassPath){
        boolean withoutException = true;
        Connection conn = null;
        try {
            conn = getDataSource(dataSourceType).getConnection();
        } catch (SQLException e) {
            log.error("数据库连接异常：",e);
            return false;
        }
        ScriptRunner runner = new ScriptRunner(conn);
        Resources.setCharset(Charset.forName("UTF-8"));//设置字符集，以免中文乱码
        runner.setLogWriter(null);//不输出运行日志，只输出错误日志
        //如果拼接整个脚本为一条sql，无论是否true，遇到错误时都停止，但不影响出错前没有问题的sql语句段的执行和提交
        //如果不拼接整个脚本，true时遇到错误停止，但不影响出错前没有问题的sql语句的执行和提交，false时则会跳过所有错误而执行所有可以执行的sql
        runner.setStopOnError(false);
        //是否提交，为true时无论是否在拼接整个脚本为一条sql，都会在最后统一提交，为false时会执行connection.rollback()，但实际也并没有回滚
        runner.setAutoCommit(true);
        //拼接整个脚本为一条sql执行
        //与不拼接的区别在于，前者是一条条执行，后者是拼接后一次性执行
        //如果想要忽略所有错误sql而执行所有可执行的sql，就不能拼接
        //即使sql语句没有语法错误也没有逻辑错误，大量执行sql的话也可能会有部分错误，因此，最好不要拼接
        runner.setSendFullScript(false);
        int success = pathRelativeClassPath.length;
        String errorList = "";
        try {
            File file = new File(ERROR_LOG_PATH);
            if(!file.exists()){
                file.mkdirs();//创建多层次不存在的目录
            }
            for (String pathX:pathRelativeClassPath) {
                try {
                    //输出错误日志：日志路径(到目录)必须存在
                    String errorLogFileName = ERROR_LOG_PATH+pathX.substring(pathX.lastIndexOf("/")+1);
                    runner.setErrorLogWriter(new PrintWriter(errorLogFileName));
                    runner.runScript(Resources.getResourceAsReader(pathX));
                    if (new File(errorLogFileName).length()>0){
                        errorList += "\n\t"+errorLogFileName;
                        success--;
                    }
                } catch (FileNotFoundException e) {
                    log.error("错误日志输出文件未找到：",e);
                }
            }
        } catch (IOException e) {
            withoutException = false;
            log.error("IO异常：",e);
        }finally {
            runner.closeConnection();
            try {
                conn.close();
            } catch (SQLException e) {
                withoutException = false;
                log.error("sql连接关闭异常：",e);
            }
            log.info(">>>>>脚本执行完成<<<<<");
            log.info("脚本总数："+pathRelativeClassPath.length+" 成功执行总数："+success);
            if (!errorList.equals("")){
                withoutException = false;
                log.info("错误日志列表："+errorList);
            }
            return withoutException;
        }
    }

    /**
     * 执行sql脚本，支持指定数据源
     * 使用ant SQLExec执行sql脚本，sql脚本一行出错会整个脚本回滚
     * 最终的执行结果：不存在数据丢失，要么全部执行，要么全部回滚
     * @param dataSourceType 数据源类型
     * @param pathRelativeClassPath sql脚本文件路径，相对classpath路径
     * @return
     */
    public boolean executeScriptWithTransaction(DataSourceType dataSourceType,String pathRelativeClassPath) {
        ComboPooledDataSource dataSource = (ComboPooledDataSource)getDataSource(dataSourceType);
        SQLExec sqlExec = new SQLExec();
        //设置数据库参数
        sqlExec.setDriver(dataSource.getDriverClass());
        sqlExec.setUrl(dataSource.getJdbcUrl());
        sqlExec.setUserid(dataSource.getUser());
        sqlExec.setPassword(dataSource.getPassword());
        //要执行的脚本
        sqlExec.setSrc(new File(LocalFileReader.getClassPath()+pathRelativeClassPath));
        //有出错的语句该如何处理：支持"continue", "stop", "abort"，默认abort
        //abort会抛出异常，返回码为1，stop不会抛出异常，返回码为0
        //continue则会忽略错误而继续运行，会执行和提交所有可正确执行的sql语句
        sqlExec.setOnerror((SQLExec.OnError)(EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
        //输出执行日志到文件中，默认输出到控制台
        sqlExec.setPrint(true);//必须设置为true才会输出到文件
        File file = new File(ERROR_LOG_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        sqlExec.setOutput(new File(file,pathRelativeClassPath.substring(pathRelativeClassPath.lastIndexOf("/")+1)));
        //必须初始化project属性，否则空指针
        sqlExec.setProject(new Project());
        try {
            sqlExec.execute();
            log.info(">>>>>脚本成功执行<<<<<");
        }catch (BuildException e){
            log.error(">>>>>SQL执行异常，全部回滚<<<<<");
            log.error("错误日志路径："+ERROR_LOG_PATH+pathRelativeClassPath.substring(pathRelativeClassPath.lastIndexOf("/")+1));
            return false;
        }
        return true;
    }

    private DataSource getDataSource(DataSourceType dataSourceType){
        if (DataSourceType.MYSQL_DB.equals(dataSourceType)){
            return dataSourceMysql;
        }else if(DataSourceType.ORACLE_DB.equals(dataSourceType)){
            return dataSourceOracle;
        }else{
            return null;
        }
    }

}
