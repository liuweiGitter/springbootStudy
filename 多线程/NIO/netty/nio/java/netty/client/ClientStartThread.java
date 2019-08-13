package netty.nio.java.netty.client;

/**
 * @author liuwei
 * @date 2019-08-13 23:38:47
 * @desc 客户端启动线程
 */
public class ClientStartThread {
	
    public static void main(String[] args) {
    	String serverAddr = "127.0.0.1";
    	int serverPort = 2019;
    	//使用5个线程模拟5个客户端
        for (int i = 0; i < 5; i++) {
            new Thread(new NettyClient(serverAddr,serverPort)).start();
        }
    }
    
    //运行结果示例
    /**
     * ----------服务端----------
	 服务器线程名称：main
	服务器线程已启动，监听端口号2019，等待客户端连接......
	00:11:35.612 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
	00:11:35.612 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
	00:11:35.612 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
	00:11:35.612 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
	00:11:35.628 [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
	00:11:35.628 [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
	00:11:35.628 [nioEventLoopGroup-3-1] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@77819aa
	通道Handler线程名称：nioEventLoopGroup-3-2
	接收到的客户端消息为：我是客户端 nioEventLoopGroup-5-1
	通道Handler线程名称：nioEventLoopGroup-3-1
	接收到的客户端消息为：我是客户端 nioEventLoopGroup-3-1
	通道Handler线程名称：nioEventLoopGroup-3-3
	接收到的客户端消息为：我是客户端 nioEventLoopGroup-2-1
	通道Handler线程名称：nioEventLoopGroup-3-4
	接收到的客户端消息为：我是客户端 nioEventLoopGroup-6-1
	通道Handler线程名称：nioEventLoopGroup-3-1
	接收到的客户端消息为：我是客户端 nioEventLoopGroup-4-1
     * ----------客户端----------
	客户端线程名称：Thread-1
	客户端发起异步连接
	通道Handler线程名称：nioEventLoopGroup-5-1
	接收到的服务端消息为：2019-08-14T00:11:35.693 服务器接收消息成功！
	通道Handler线程名称：nioEventLoopGroup-3-1
	接收到的服务端消息为：2019-08-14T00:11:35.692 服务器接收消息成功！
	客户端线程名称：Thread-2
	客户端发起异步连接
	客户端线程名称：Thread-0
	客户端发起异步连接
	通道Handler线程名称：nioEventLoopGroup-2-1
	接收到的服务端消息为：2019-08-14T00:11:35.777 服务器接收消息成功！
	客户端线程名称：Thread-4
	客户端发起异步连接
	通道Handler线程名称：nioEventLoopGroup-6-1
	接收到的服务端消息为：2019-08-14T00:11:35.809 服务器接收消息成功！
	通道Handler线程名称：nioEventLoopGroup-4-1
	接收到的服务端消息为：2019-08-14T00:11:35.941 服务器接收消息成功！
	客户端线程名称：Thread-3
	客户端发起异步连接
	00:11:37.983 [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 1 thread-local buffer(s) from thread: nioEventLoopGroup-3-1
	00:11:38.015 [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 1 thread-local buffer(s) from thread: nioEventLoopGroup-2-1
	00:11:38.015 [nioEventLoopGroup-5-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 1 thread-local buffer(s) from thread: nioEventLoopGroup-5-1
	00:11:38.046 [nioEventLoopGroup-6-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 1 thread-local buffer(s) from thread: nioEventLoopGroup-6-1
	00:11:38.187 [nioEventLoopGroup-4-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 1 thread-local buffer(s) from thread: nioEventLoopGroup-4-1
     */
    
}
