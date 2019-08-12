package netty.nio.java.nio.selector.socketserver;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * @author liuwei
 * @date 2019-08-12 14:43
 * @desc 服务端的选择器线程
 */
@Slf4j
public class SelectorThread implements Runnable{

    // 缓冲区大小
    private final int BUFFER_SIZE = 1024;

    // 超时时间，单位毫秒
    private int timeOut = 3000;

    private Selector selector;

    public SelectorThread(Selector selector){
        this.selector=selector;
        new Thread(this).start();
    }

    @Override
    public void run() {
        // 创建服务端TCP协议通道事件处理器，处理各种通道事件
        TCPEventHandler eventHandler = new TCPEventHandlerMan(BUFFER_SIZE);

        Iterator<SelectionKey> keyIter = null;
        SelectionKey key = null;

        // 反复循环，等待客户端连接
        while(true) {
            /**
             * 对所有注册的通道最多阻塞TIME_OUT毫秒，等待其就绪
             * 如果没有通道在TIME_OUT毫秒内就绪(即value==0)，继续下一轮轮询
             * 如果有通道在TIME_OUT毫秒内就绪(即value>0)，遍历selectedKeys进行IO处理
             */
            try {
                if (selector.select(timeOut) == 0) {
                    //System.out.println("I am waiting for you.");
                    continue;
                }
            } catch (IOException e) {
                log.info(""+e);
            }
            // 取得selectedKeys()的迭代器，遍历每一个已经准备就绪的通道
            keyIter = selector.selectedKeys().iterator();
            try{
                while (keyIter.hasNext()) {
                    key = keyIter.next();

                    if (key.isAcceptable()) {
                        // 有客户端连接请求时
                        eventHandler.handleAccept(key);
                    }
                    if (key.isReadable()) {
                        // 从客户端读取数据
                        eventHandler.handleRead(key);
                    }
                    if (key.isValid() && key.isWritable()) {
                        // 向客户端写入数据
                        eventHandler.handleWrite(key);
                    }

                    /**
                     * 在当前选中的SelectionKey集合中删除已处理的key
                     * 注意，只是删除已选中的key，在注册集合中该key仍存在
                     * 实际上，此步骤完全没有必要，因为迭代器本轮遍历完以后，下一次会被重新赋值
                     */
                    keyIter.remove();

                }
            }catch (IOException e){
                /**
                 * 取消当前key对应通道的注册
                 * 即使客户端关闭了或不可连接了，selector中仍注册有该通道，且selectedKeys中仍保持有该通道
                 * 如果客户端关闭了或不可连接了，则会触发此处的异常
                 * 如果此处不进行通道的注销，则每一次的selectedKeys迭代器都会触发此处异常，将无限循环异常的捕获和处理
                 * 对于此处，将无限打印"通道处理异常：xxx"
                 */
                key.cancel();
                log.info("通道处理异常："+e);
            }
        }
    }
}
