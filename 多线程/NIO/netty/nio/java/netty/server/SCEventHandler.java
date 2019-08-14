package netty.nio.java.netty.server;

import java.time.LocalDateTime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import netty.nio.java.netty.constant.CodeConstant;
import netty.nio.java.netty.constant.SystemConstant;

/**
 * @author liuwei
 * @date 2019-08-13 22:55:31
 * @desc 服务端网络通道事件处理器类
 */
@Slf4j
public class SCEventHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 通道读事件就绪后的处理器
	 * @param ctx 通道上下文对象
	 * @param msg 通道中接收到的客户端消息的缓冲区数据对象
	 * 	注：这个msg指的并不是客户端发来的原始消息，而是原始消息经过协议包装后的数据包消息
	 * 	如果这个协议消息经过粘包解码、字符串解码等操作，就可以还原出原始消息
	 * 	本例中服务器的通道事件处理器类在处理器责任链中添加了解码操作，因此还原出了原始消息
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//打印接收到的客户端消息
		String recive = (String) msg;
		System.out.println("通道Handler线程名称："+Thread.currentThread().getName());
		System.out.println("接收到的客户端消息为：" + recive);

		/**
		 * 创建一个新的缓冲区，写入服务器的响应信息，然后将缓冲区写入通道发送到客户端
		 * 通道写入缓冲区数据的操作是异步非阻塞的
		 */
		String respMsg = LocalDateTime.now()+" 服务器接收消息成功！"+SystemConstant.LINE_SEPARATOR;
		ByteBuf respByteBuf = Unpooled.copiedBuffer(respMsg.getBytes(CodeConstant.UTF8_STR));
		ctx.write(respByteBuf);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		/**
		 * Netty中通道上下文的write方法只是把待发送的消息放到发送缓存数组中，
		 * 在调用 flush方法后，才会将发送缓冲区的消息写入到 SocketChannel中发送给客户端
		 */
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//当发生异常时，关闭通道上下文，并释放和它相关联的句柄等资源
		log.error("通道异常："+cause);
		ctx.close();
	}

}
