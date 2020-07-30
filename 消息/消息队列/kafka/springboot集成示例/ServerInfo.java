package com.ping.job.manager.boot;

import com.ping.job.manager.util.ShellCommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * @author liuwei
 * @date 2020-07-09 15:31
 * @desc 服务元数据信息
 * 在服务启动之前，可以获取到服务ip地址信息
 * 在服务启动之后，可以获取到服务端口信息
 */
@Component
@Slf4j
public class ServerInfo {

    @Autowired
    private Environment environment;

    //项目名称(不允许为空)
    private static String webName;
    //服务端口
    private static int serverPort;
    //ip地址
    private static String ipAddr;
    //ip地址开头
    private static String ipStartsWith;

    @PostConstruct
    private void init(){
        ipStartsWith = environment.getProperty("server_ip_starts");
        if (null == ipStartsWith || ipStartsWith.isEmpty()) {
            ipStartsWith = "172";
        }
        serverPort = Integer.parseInt(environment.getProperty("server.port"));
    }

    //获取 "ip地址:端口/项目名称"
    public String getUrl() {
        return "http://" + getIpAddr() +":"+ getServerPort() + getWebName();
    }

    public String getWebName() {
        webName = MetaDataCache.ENV.getProperty("server.servlet.context-path");
        if (StringUtils.isEmpty(webName.trim())) {
            log.error("server.servlet.context-path 不能为空!");
            System.exit(666);
        }
        return webName.trim();
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getIpAddr() {
        if (null != ipAddr) {
            return ipAddr;
        }
        //注意：centos和ubuntu的ifconfig命令输出结果数据格式是不一样的，需要代码来识别
        String command = "ifconfig | grep inet";
        List<String> multiResult = null;
        try {
            multiResult = ShellCommandUtil.cmdMultiResult(command);
        } catch (IOException e) {
            exit(e.getMessage(),666);
        }
        log.info("当前服务器ip地址信息列表：");
        for (String result : multiResult) {
            log.info(result);
            if (result.contains(ipStartsWith) && null == ipAddr) {
                String[] addrStrings = result.split(" ");
                for (String addrString : addrStrings) {
                    if (addrString.startsWith(ipStartsWith)) {
                        ipAddr = addrString;
                        break;
                    }
                }
            }
        }
        log.info("当前服务器"+ipStartsWith+"段ip地址："+ipAddr);
        if (null == ipAddr) {
            exit("ip地址获取失败！请指定正确的ip地址！",666);
        }
        return ipAddr;
    }

    private static void exit(String errMsg, int code){
        log.error(errMsg);
        System.exit(code);
    }

}
