package netty.nio.java.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-08-13 23:58:22
 * @desc 客户端网络通道事件处理器类
 */
@Slf4j
public class CCEventHandler extends ChannelInboundHandlerAdapter {

	//通道连接事件就绪后的处理器：向服务器发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	//消息写入缓冲区
        String reqMsg = "我是客户端 " + Thread.currentThread().getName();
        byte[] reqMsgByte = reqMsg.getBytes("UTF-8");
        ByteBuf reqByteBuf = Unpooled.buffer(reqMsgByte.length);
        reqByteBuf.writeBytes(reqMsgByte);
        //通道写入并刷新(发送)消息到服务器
        ctx.writeAndFlush(reqByteBuf);
    }
 
    /**
	 * 通道读事件就绪后的处理器：读取来自服务器的消息
	 * @param ctx 通道上下文对象
	 * @param msg 通道中接收到的服务端消息的缓冲区对象载体
	 * @throws Exception
	 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String recive = new String(req, "UTF-8");
        System.out.println("通道Handler线程名称："+Thread.currentThread().getName());
		System.out.println("接收到的服务端消息为：" + recive);
		ctx.close();
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	//当发生异常时，关闭通道上下文，并释放和它相关联的句柄等资源
		log.error("通道异常："+cause);
		ctx.close();
    }

}
