package netty.nio.java.netty.server;

import java.time.LocalDateTime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
	 * @param msg 通道中接收到的客户端消息的缓冲区对象载体
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//获取通道的缓冲区对象
		ByteBuf buf = (ByteBuf) msg;
		//获取缓冲区可读字节数，动态创建字节数组，并将缓冲区数据写入字节数组
		byte[] reg = new byte[buf.readableBytes()];
		buf.readBytes(reg);
		//打印接收到的客户端消息
		String recive = new String(reg, "UTF-8");
		System.out.println("通道Handler线程名称："+Thread.currentThread().getName());
		System.out.println("接收到的客户端消息为：" + recive);

		/**
		 * 创建一个新的缓冲区，写入服务器的响应信息，然后将缓冲区写入通道发送到客户端
		 * 通道写入缓冲区数据的操作是异步非阻塞的
		 */
		String respMsg = LocalDateTime.now()+" 服务器接收消息成功！";
		ByteBuf respByteBuf = Unpooled.copiedBuffer(respMsg.getBytes());
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
