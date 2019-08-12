package netty.nio.java.nio.selector.socketclient;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author liuwei
 * @date 2019-08-12 10:27
 * @desc 客户端选择器线程
 * 用以管理和处理客户端通道的IO
 */
@Slf4j
public class SelectorThread implements Runnable{
    private Selector selector;

    public SelectorThread(Selector selector){
        this.selector=selector;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            while (selector.select() > 0) {
                // 遍历注册的每个Channel：通过其选择器key遍历
                for (SelectionKey sk : selector.selectedKeys()) {
                    // 如果该SelectionKey对应的Channel中有可读的数据
                    if (sk.isReadable()) {
                        // 获取通道，读取数据到字节缓冲区
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        sc.read(buffer);
                        buffer.flip();

                        // 将缓冲区字节转化为UTF-8字符串
                        String receivedString= Charset.forName("UTF-8").newDecoder().decode(buffer).toString();

                        // 打印来自服务器的响应信息
                        System.out.println("接收到来自服务器"+sc.socket().getRemoteSocketAddress()+"的信息:\n"+receivedString);
                    }
                    /**
                     * 在当前选中的SelectionKey集合中删除已处理的key：在注册集合中仍存在
                     * A key is valid upon creation and remains so
                     * until it is cancelled, its channel is closed, or its selector is closed.
                     */
                    selector.selectedKeys().remove(sk);
                }
            }
        } catch (IOException ex) {
            /**
             * 由于客户端的选择器只注册了一个通道，因此，只要捕获到任何异常，结束整个选择器线程即可
             * 如果注册了多个通道，则需要移除有问题的通道的注册，并保持选择器线程继续运行，详参服务端选择器线程的处理逻辑
             */
            log.info(""+ex);
        }
    }
}
