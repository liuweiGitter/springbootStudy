package netty.nio.java.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.compression.Bzip2Decoder;
import io.netty.handler.codec.compression.Bzip2Encoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import netty.nio.java.netty.constant.CodeConstant;

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
			/**
			 * 设置处理器的责任链，先进行缓冲区的粘包解码，然后进行字符串解码，最后处理解码后的消息
			 * 1.LineBasedFrameDecoder粘包解码时设置单条消息的最大长度为1024字节，超长报异常
			 * 以换行符"\n"或"\r\n"作为消息的结束标志
			 * 要求客户端发送的消息必须以换行符结尾，否则无法解析，从而终止责任链的传递
			 * 由于通道事件处理器在责任链最后一环，终止责任链意味着不会进行通道事件处理
			 * 2.StringDecoder用于将粘包解码后的对象转为字符串列表，代码就一行：
			 * 	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception
			 * 	{
			 * 		out.add(msg.toString(this.charset));
			 * 	}
			 * 如果不设置解码字符集，会采用JVM和操作系统的默认字符集，这个字符集是不确定的
			 * 如果编码解码都用默认字符集，由于通信双方在不同的主机上运行，字符集很有可能是不一样的
			 * 因此，必须明确约定字符集
			 */
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024))
			.addLast(new StringDecoder(CodeConstant.UTF8_CHAR))
			.addLast(new SCEventHandler());
			justDemo1(true);
			justDemo2(true);
		}
		
		//分隔符解码器和定长解码器
		private void justDemo1(boolean stop) {
			if (stop) {
				return;
			}
			/**
			 * LineBasedFrameDecoder可称为换行解码器
			 * 与之相似的还有更通用的分隔符解码器DelimiterBasedFrameDecoder
			 * 双方约定分隔符后，发送消息时尾部添加分隔符，解析时使用分隔符解码器
			 * 下文举例分隔符解码器的创建，实际应用中放在处理器责任链粘包解码的位置(即第一责任)即可
			 * 注意，分隔符要足够稀有，不能出现在消息内容中，本例中设置分隔符为$_
			 */
			ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
			new DelimiterBasedFrameDecoder(1024,delimiter);
			
			/**
			 * 按照双方约定，每次发送的消息长度固定时，可用定长解码器解码消息
			 * 只要履行约定，定长解码器不存在拆包粘包问题，是配置最简单的解码器
			 * 如果发送的消息不足约定的长度，则视作半包，消息接收后不会被解码，而是缓存起来
			 * 直到下一个消息过来拼够约定的长度，然后才会流转到责任链下一环(即字符串解码)
			 * 如果发送的消息长度大于约定长度，则将最后的余数视作半包
			 */
			new FixedLengthFrameDecoder(64);
		}
		
		//消息长度编码器和解码器
		private void justDemo2(boolean stop) {
			if (stop) {
				return;
			}
			/**
			 * 消息长度编码器和解码器要配套使用
			 * 
			 * 一个消息可以经过重重编码，再发送出去
			 * 如先进行二进制(即字节)编码，然后进行消息长度编码，然后压缩码流，最后才发出
			 * 那么这条消息在接收方，必须重重逆序解码，才能被还原
			 * 如先进行码流的解压缩，然后进行消息长度解码，然后进行二进制(即字节)解码，最后才还原出源消息
			 * 
			 * 大多数编码方案不像定长、换行、分隔符那样简单到只需要双方约定简单的编码协议后添加对应的解码器即可
			 * 这些编码方案需要靠编码器来实现，而不仅仅是靠私有实现
			 * 因此这些方案需要编码器和解码器的配套使用
			 * 具体表现在，处理器责任链中要同时注册相对应的编码器和解码器
			 * 
			 * ☆五星注意☆
			 * 当需要多重编码时，编码器和解码器的顺序至关重要
			 * 为维持顺序并增强可读性，现约定，责任链的顺序为：
			 * 解码器n-->...-->解码器1-->编码器n-->...-->编码器1-->通道事件处理器ChannelInboundHandlerAdapter
			 * 这是因为发送消息时，责任链逆序执行，先走通道事件处理器，然后编码器1，一路逆行，直到所有编码完成后，通道再发出消息
			 * 而接收消息时，责任链顺序执行，通道接收到消息后，先走解码器n，然后解码器n-1，一路顺行，直到所有解码完成然后来到通道事件处理器
			 * 通道的管道能自动区分责任链环是属于编码器、解码器或是通道事件处理器，编码时跳过解码器，解码时跳过编码器
			 * 
			 * ☆五星注意☆
			 * 除了以上责任链顺序，务必需要注意的是，编码时必须要有编码器能对半包进行标记，即能处理粘包问题
			 * 有些编码器仅负责编码而不处理粘包，如一些不带定长、换行、分隔符、消息长度的二进制字节编码/压缩编码等
			 * 编码链上必须注意这一点
			 * 
			 * 本例中使用了protobuf32位消息长度编码器和解码器
			 * Netty中还有其它的消息长度编码解码器类，如最通用的LengthFieldPrepender和LengthFieldBasedFrameDecoder，不赘述
			 * 
			 * 本例对ProtobufDecoder构造时传参为null，仅做示例
			 * ProtoBuf编解码需要特殊格式的类支持，具体内容，参见《Netty权威指南》
			 */
			SocketChannel channel = new NioSocketChannel();
			channel.pipeline()
			.addLast("bzip解压码流",new Bzip2Decoder())
			.addLast("protobuf消息长度解码",new ProtobufVarint32FrameDecoder())
			.addLast("protobuf二进制解码",new ProtobufDecoder(null))
			.addLast("bzip压缩码流",new Bzip2Encoder())
			.addLast("protobuf消息长度编码，消息前缀添加32bit表征长度",new ProtobufVarint32LengthFieldPrepender())
			.addLast("protobuf二进制编码",new ProtobufEncoder())
			.addLast(new SCEventHandler());
		}
		
	}

}
