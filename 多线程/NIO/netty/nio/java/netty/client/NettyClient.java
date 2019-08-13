package netty.nio.java.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author liuwei
 * @date 2019-08-13 23:34:21
 * @desc Netty客户端
 */
public class NettyClient implements Runnable {

	private int serverPort;
	private String serverAddr;

	public NettyClient(String serverAddr, int serverPort) {
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
	}

	@Override
	public void run() {
		connect(serverAddr, serverPort);
	}

	public void connect(String host, int port) {
		//创建客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			//创建客户端的辅助启动对象ServerBootstrap，并进行NIO线程组、NIO通道、网络事件处理器等选项的配置
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new CCEventHandler());
						}
					});

			//发起异步连接，并同步等待连接完成
			ChannelFuture f = b.connect(host, port).sync();
			System.out.println("客户端线程名称："+Thread.currentThread().getName());
			System.out.println("客户端发起异步连接");

			//线程阻塞，直到客户端连接关闭之后，返回结果，走向finally
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
	}

}
