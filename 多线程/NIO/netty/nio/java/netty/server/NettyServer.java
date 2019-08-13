package netty.nio.java.netty.server;

import io.netty.channel.socket.SocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-08-13 22:21:28
 * @desc Netty服务端
 */
@Slf4j
public class NettyServer {

	public NettyServer(){
		
	}
	
	public NettyServer(int port){
		bind(port);
	}
	
	public void bind(int port) {
		/**
		 * 配置服务端的 NIO线程池，用于网络事件处理
		 * NIO线程组acceptorGroup用于处理客户端连接事件
		 * NIO线程组workerGroup用于处理通道读写事件
		 */
		EventLoopGroup acceptorGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//创建服务端的辅助启动对象ServerBootstrap，并进行NIO线程组、NIO通道、网络事件处理器等选项的配置
			ServerBootstrap b = new ServerBootstrap();
			b.group(acceptorGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new ChildChannelHandler());

			//绑定监听端口，并同步等待绑定操作完成，绑定完成后，服务器开始监听连接
			ChannelFuture f = b.bind(port).sync();
			System.out.println("服务器线程名称："+Thread.currentThread().getName());
			System.out.println("服务器线程已启动，监听端口号"+port+"，等待客户端连接......");
			
			//线程阻塞，直到服务器连接关闭之后，返回结果，走向finally
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("服务器线程中断："+e);
		} finally {
			//优雅退出，释放线程池资源
			acceptorGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	//网络通道事件处理器类
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new SCEventHandler());
		}
	}

}
