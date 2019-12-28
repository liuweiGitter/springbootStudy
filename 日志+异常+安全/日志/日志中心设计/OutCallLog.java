package com.jshx.zq.p2p.log;

import com.jshx.zq.p2p.util.UUIDUtils;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author liuwei
 * @date 2019-12-24 21:07
 * @desc 外部接口调用日志bean
 */
@Data
public class OutCallLog {

    //日志id
    private String id;

    //调用者：即登录用户
    private String caller;

    //调用者ip
    private String clientIp;

    //调用发起时间
    private LocalDateTime callTime;

    //连接耗时(ms)
    private long connCost;

    //响应耗时(ms)
    private long responseCost;

    //调用url
    private String url;

    //接口协议类型
    private String protocol;

    //调用点类名+方法名：以定位问题代码
    private String pointer;

    //调用过程是否正常
    private boolean success;

    //错误信息
    private String errorMsg;

    public OutCallLog(){
        this.id = UUIDUtils.getUUid();
    }

    public OutCallLog(String caller, LocalDateTime callTime, String url, String pointer, String clientIp, String protocol) {
        this.id = UUIDUtils.getUUid();
        this.caller = caller;
        this.callTime = callTime;
        this.url = url;
        this.pointer = pointer;
        this.clientIp = clientIp;
        this.protocol = protocol;
    }

    public OutCallLog addCallResult(long connCost, long responseCost, boolean success, String errorMsg) {
        this.connCost = connCost;
        this.responseCost = responseCost;
        this.success = success;
        this.errorMsg = errorMsg;
        return this;
    }

}
