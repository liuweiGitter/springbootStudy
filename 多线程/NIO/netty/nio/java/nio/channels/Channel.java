package netty.nio.java.nio.channels;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liuwei
 * @date 2019-08-09 16:36
 * @desc 通道
 */
public class Channel {
    /**
     * 通道存放缓冲数据，通信一方写入通道数据到缓冲区，另一方读取数据自缓冲区
     * 缓冲区的数据读写可以是异步的
     * 常见的通道类型：
     * 1.文件IO通道
     * FileChannel
     * 2.网络IO通道
     * DatagramChannel(UDP通道)
     * SocketChannel(TCP通道)
     * ServerSocketChannel(TCP通道)
     */

    private static String filePath = "C:\\Users\\Administrator\\Desktop\\123.txt";

    public static void main(String[] args) throws IOException {
        //文件通道
        FileChannelDemo.byteBuffer(filePath);
    }

}
