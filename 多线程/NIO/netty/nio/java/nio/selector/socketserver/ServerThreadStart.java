package netty.nio.java.nio.selector.socketserver;

/**
 * @author liuwei
 * @date 2019-08-12 11:00
 * @desc 服务端线程运行入口
 */
public class ServerThreadStart {

    public static void main(String[] args) {
        System.out.println(">>>启动服务端");
        int port = 10086;
        new SocketServer(port);
    }

    /**
     * 测试启动服务端主线程后，接连启动2个客户端，服务端输出结果如下：
     *
     * >>>启动服务端
     * 接收到来自/127.0.0.1:58971的信息:
     * 客户端1请求1 by liuwei 2019-08-12T16:37:14.063
     * 接收到来自/127.0.0.1:58981的信息:
     * 客户端2请求1 by liuwei 2019-08-12T16:37:18.853
     * 接收到来自/127.0.0.1:58971的信息:
     * 客户端1请求2 by liuwei 2019-08-12T16:37:20.077
     * 接收到来自/127.0.0.1:58981的信息:
     * 客户端2请求2 by liuwei 2019-08-12T16:37:21.855
     *
     * 可以看到，服务端单线程的selector同时交替处理了2个通道的IO数据
     */
}
