package netty.nio.java.nio.channels;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liuwei
 * @date 2019-08-09 17:30
 * @desc 文件通道
 */
public class FileChannelDemo {

    //字节缓冲
    public static void byteBuffer(String filePath) throws IOException {
        //目标文件
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        //文件的通道
        java.nio.channels.FileChannel inChannel = file.getChannel();
        //缓冲区对象(使用字节缓冲区，并设置缓冲区大小)
        ByteBuffer buf = ByteBuffer.allocate(64);
        //通道读取数据到缓冲区
        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1) {
            //翻转缓冲区：虽然还不知道为什么，但此为必须步骤
            buf.flip();
            //遍历读取缓冲区数据
            while(buf.hasRemaining()){
                System.out.print((char) buf.get());
            }
            //清空缓冲区
            buf.clear();
            //再次读数据到缓冲区
            bytesRead = inChannel.read(buf);
        }
        //关闭通道
        file.close();
    }

    //字符缓冲 https://ifeve.com/channels/
    public static void charBuffer(String filePath) throws IOException {
        //目标文件
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        //文件的通道
        FileChannel inChannel = file.getChannel();
        //缓冲区对象(使用字节缓冲区，并设置缓冲区大小)
        CharBuffer buf = CharBuffer.allocate(64);
        //通道读取数据到缓冲区
        int charsRead = inChannel.;
        while (charsRead != -1) {
            //翻转缓冲区：虽然还不知道为什么，但此为必须步骤
            buf.flip();
            //遍历读取缓冲区数据
            while(buf.hasRemaining()){
                System.out.print((char) buf.get());
            }
            //清空缓冲区
            buf.clear();
            //再次读数据到缓冲区
            charsRead = inChannel.read(buf);
        }
        //关闭通道
        file.close();
    }


}
