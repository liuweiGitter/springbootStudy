package netty.nio.java.nio.selector.socketclient;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author liuwei
 * @date 2019-08-12 08:56
 * @desc 客户端2线程运行入口
 * 测试时，启动一个服务端线程，多个客户端线程，以模拟服务端单线程selector处理多通道的功能
 */
public class Client2ThreadStart {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println(">>>启动客户端2");
        SocketClient client = new SocketClient("127.0.0.1",10086);

        client.sendMsg("客户端2请求1 by liuwei "+ LocalDateTime.now());
        Thread.sleep(3000);
        client.sendMsg("客户端2请求2 by liuwei "+ LocalDateTime.now());

    }

}
