package netty.nio.java.nio.selector.socketserver;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author liuwei
 * @date 2019-08-12 10:26
 * @desc 服务端TCP协议通道事件处理器接口
 */

public interface TCPEventHandler{

    /**
     * 处理一个SocketChannel通道的连接事件
     */
    void handleAccept(SelectionKey key) throws IOException;

    /**
     * 处理一个SocketChannel通道的读取事件
     */
    void handleRead(SelectionKey key) throws IOException;

    /**
     * 处理一个SocketChannel通道的写入事件
     */
    void handleWrite(SelectionKey key) throws IOException;

}
