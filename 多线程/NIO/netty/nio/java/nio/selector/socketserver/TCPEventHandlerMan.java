package netty.nio.java.nio.selector.socketserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author liuwei
 * @date 2019-08-12 10:38
 * @desc 服务端TCP协议通道事件处理器
 */
public class TCPEventHandlerMan implements TCPEventHandler {

    private int bufferSize;

    public TCPEventHandlerMan(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        //客户端连接请求到来时，创建一个SocketChannel
        SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
        clientChannel.configureBlocking(false);
        //注册该SocketChannel到选择器
        clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        // 获得与客户端通信的通道
        SocketChannel clientChannel = (SocketChannel)key.channel();

        // 获取并清空缓冲区
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        buffer.clear();

        // 读取来自客户端的消息到缓冲区
        long bytesRead = clientChannel.read(buffer);

        if(bytesRead == -1){
            // 客户端未发送消息时，关闭
            clientChannel.close();
        }else{
            // 读取缓冲区消息
            buffer.flip();

            // 将字节转化为为UTF-8的字符串   
            String receivedString = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();

            // 打印客户端信息
            System.out.println("接收到来自"+clientChannel.socket().getRemoteSocketAddress()+"的信息:\n"+receivedString);

            // 响应客户端信息
            String sendString = "我是服务端，已于@"+new Date().toString()+"收到你的信息："+receivedString;
            buffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
            clientChannel.write(buffer);

            // 为下一次读取或写入做准备
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {

    }
}
