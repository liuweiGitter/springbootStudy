package netty.nio.java.nio.selector.socketclient;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author liuwei
 * @date 2019-08-12 08:56
 * @desc 客户端1线程运行入口
 * 测试时，启动一个服务端线程，多个客户端线程，以模拟服务端单线程selector处理多通道的功能
 */
public class Client1ThreadStart {

    public static void main(String[] args) throws IOException, InterruptedException {
        /**
         * 服务器要先于客户端启动，否则报错
         * Exception in thread "main" java.net.ConnectException: Connection refused: connect
         * 线程将异常结束
         * Process finished with exit code 1
         */

        System.out.println(">>>启动客户端1");
        SocketClient client = new SocketClient("127.0.0.1",10086);

        client.sendMsg("客户端1请求1 by liuwei "+ LocalDateTime.now());
        Thread.sleep(6000);
        client.sendMsg("客户端1请求2 by liuwei "+ LocalDateTime.now());

        /**
         * 如果在收到响应消息前关掉了客户端通道，将接收不到响应的消息
         * 本例中，由于发送第一条消息后休眠了一段时间，因此，即使此处关掉客户端，也可以收到第一条响应
         */
        //client.closeChannel();
        //System.out.println(">>>关闭客户端");

        /**
         * 根据本范例代码逻辑
         *
         * 如果客户端保持连接，但远程主机关闭了本连接，客户端的选择器线程中将抛出异常如下：
         * 14:24:49.136 logback [Thread-0] INFO  n.n.j.n.s.s.SelectorThread - java.io.IOException: 远程主机强迫关闭了一个现有的连接。
         * 至此，本例中选择器中已经没有了注册的通道，选择器线程结束，整个客户端线程也因此结束，线程将正常结束：
         * Process finished with exit code 0
         *
         * 如果服务端保持连接，但客户端关闭了本连接，服务端线程中将抛出异常如下：
         * 14:35:52.514 logback [main] INFO  n.n.j.n.s.socketserver.SocketServer - java.io.IOException: 远程主机强迫关闭了一个现有的连接。
         * 至此，本例中选择器中已经没有了注册的通道，选择器线程结束，整个客户端线程也因此结束，线程将正常结束：
         * Process finished with exit code 0
         * 但在服务端，其选择器线程只会移除本通道的注册，并继续保持对其它通道的监听
         */
    }

}
