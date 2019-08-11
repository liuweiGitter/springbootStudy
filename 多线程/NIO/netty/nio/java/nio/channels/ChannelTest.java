package netty.nio.java.nio.channels;

import java.io.File;
import java.io.IOException;

/**
 * @author liuwei
 * @date 2019-08-09 16:36
 * @desc 通道
 */
public class ChannelTest {
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
    	fileChannel(false);
    }
    
    private static void fileChannel(boolean isStart) throws IOException {
    	if (!isStart) {
			return;
		}
    	File fileTemp = new File(filePath);
		File parent = fileTemp.getParentFile();
		if (!parent.exists()) parent.mkdirs();
		if (!fileTemp.exists()) fileTemp.createNewFile();
		//文件通道：字节读
        //FileChannelDemo.byteBuffer(filePath);
        //文件通道：字符读，根据文件的具体编码来选择解码，中文字符编码常见有GBK/GB2312/UTF-8
        //FileChannelDemo.charBuffer(filePath,"GB2312");
    	//文件通道：字节写，追加或覆盖
    	//FileChannelDemo.byteBufferWrite(filePath,true);
    	//文件通道：字节写，指定位置插入或覆盖
    	//FileChannelDemo.byteBufferWrite(filePath,5,true);
    }


}
