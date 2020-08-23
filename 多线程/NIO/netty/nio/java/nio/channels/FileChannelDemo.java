package netty.nio.java.nio.channels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.time.LocalDateTime;

/**
 * @author liuwei
 * @date 2019-08-09 17:30
 * @desc 文件通道
 * 示例最常见的字节缓冲和字符缓冲的读写
 * 
 * FileChannel是一个连接到文件的通道
 * 可以通过文件通道读写文件
 * FileChannel无法设置为非阻塞模式，它总是运行在阻塞模式下
 */
public class FileChannelDemo {
	
	private static final int BUFFER_SIZE = 1024;

	// 通过字节缓冲读文件
	public static void byteBuffer(String filePath) throws IOException {
		// 目标文件
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		// 文件的通道
		FileChannel inChannel = file.getChannel();
		// 缓冲区对象(使用字节缓冲区，并设置缓冲区大小)
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		// 通道读取数据到缓冲区
		int bytesRead = inChannel.read(buf);
		while (bytesRead != -1) {
			// 切换buffer从写模式到读模式
			buf.flip();
			// 遍历读取缓冲区数据
			while (buf.hasRemaining()) {
				System.out.print((char) buf.get());
			}
			// 清空缓冲区
			buf.clear();
			// 再次读数据到缓冲区
			bytesRead = inChannel.read(buf);
		}
		// 关闭通道
		inChannel.close();
		// 关闭文件
		file.close();
	}

	// 通过字符缓冲(需要通过字节缓冲转换)读文件
	public static void charBuffer(String filePath, String charsetName) throws IOException {
		Charset charset = Charset.forName(charsetName);
		CharsetDecoder decoder = charset.newDecoder();
		// 目标文件
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		// 文件的通道
		FileChannel inChannel = file.getChannel();
		// 缓冲区对象(字节缓冲区转字符缓冲区)
		ByteBuffer bBuf = ByteBuffer.allocate(BUFFER_SIZE);
		//对于UTF算法，2个字节可能转为2个字符，因此cBuf的容量应该等于bBuf的容量
		CharBuffer cBuf = CharBuffer.allocate(BUFFER_SIZE);
		// 通道读取数据到缓冲区
		int bytesRead = inChannel.read(bBuf);
		while (bytesRead != -1) {
			// 切换buffer从写模式到读模式
			bBuf.flip();
			// 以charsetName编码转换ByteBuffer到CharBuffer
			decoder.decode(bBuf, cBuf, true);
			// 切换buffer从写模式到读模式
			cBuf.flip();
			while (cBuf.hasRemaining()) {
				System.out.print(cBuf.get());
			}
			// 重排字节缓冲区：清空已读的字节，并将本次未读到的字节排入缓冲区队首，以待和下次缓冲字节一起读取
			bBuf.compact();
			// 清空字符缓冲区
			cBuf.clear();
			bytesRead = inChannel.read(bBuf);
		}
		// 关闭通道
		inChannel.close();
		// 关闭文件
		file.close();
	}
	
	// 通过字节缓冲写文件：末尾追加或全文覆盖文件
	public static void byteBufferWrite(String filePath,boolean append) throws IOException {
		/**
		 * 追加或覆盖写入常用的语法有2种
		 */
		//方式1
		// 目标文件
		/*RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		// 文件的通道
		FileChannel outChannel = file.getChannel();
		if (append) {//追加文件
			outChannel.position(file.length());
		}else {//覆盖文件(默认)
			outChannel.position(0);
		}*/
		//方式2
		FileOutputStream file = new FileOutputStream(filePath, append);
		FileChannel outChannel = file.getChannel();
		// 缓冲区对象(使用字节缓冲区，并设置缓冲区大小)
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		// 写数据到缓冲区
		String data = LocalDateTime.now()+":liuwei 2019-08-11 nanjing ";
		int dataLength = data.length();
		int circleSize = dataLength%BUFFER_SIZE==0
				?dataLength/BUFFER_SIZE:dataLength/BUFFER_SIZE+1;
		int endIndex = 0;
		for (int i = 0; i < circleSize; i++) {
			endIndex = Math.min((i+1)*BUFFER_SIZE, dataLength);
			buf.put(data.substring(i*BUFFER_SIZE, endIndex).getBytes());
			// 通道从缓冲区读数据(到文件)
			buf.flip();
			/**
			 * 注：无论是覆盖还是追加，write之后并不会立即刷新文件，而是驻留内存
			 * 不过，write会很快写入文件，而且按照顺序写入
			 * 同一个通道内，在写入一组数据后，position会自动移动到写入的数据末尾，即光标处，等待下一次写入
			 */
			outChannel.write(buf);
			// 清理buffer
			buf.clear();
		}
		// 关闭通道
		outChannel.close();
		// 关闭文件
		file.close();
	}
	
	// 通过字节缓冲写文件：指定下标插入或覆盖(下标之后的所有数据)
	public static void byteBufferWrite(String filePath,int index,boolean replace) throws IOException {
		// 目标文件
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		// 文件的通道
		FileChannel outChannel = file.getChannel();
		// 缓冲区对象(使用字节缓冲区，并设置缓冲区大小)
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		// 写数据到缓冲区
		String data = LocalDateTime.now()+":liuwei:nanjing ";
		int dataLength = data.length();
		int circleSize = dataLength%BUFFER_SIZE==0
				?dataLength/BUFFER_SIZE:dataLength/BUFFER_SIZE+1;
		int endIndex = 0;
		if (index>file.length()) {
			index = (int) file.length();
		}else if(index<0) {
			index = 0;
		}
		outChannel.position(index);
		for (int i = 0; i < circleSize; i++) {
			endIndex = Math.min((i+1)*BUFFER_SIZE, dataLength);
			buf.put(data.substring(i*BUFFER_SIZE, endIndex).getBytes());
			// 通道从缓冲区读数据(到文件)
			buf.flip();
			if (replace) {//覆盖数据
				outChannel.write(buf);
			}else {//插入数据
				//TODO 暂无法插入
			}
			// 清理buffer
			buf.clear();
		}
		// 关闭通道
		outChannel.close();
		// 关闭文件
		file.close();
	}
	
	/**
     * 文件通道追加写入字符串，并清空字节缓存
     * @param value String字符串
     * @param fileChannel 文件通道
     * @param buf 字节缓存
     * @param file 通道指向的文件
     * @throws IOException 流写入异常
     */
    private static void flushStringValue(String value, FileChannel fileChannel, ByteBuffer buf, RandomAccessFile file) throws IOException {
        // 1.字符串转为字节数组，并写入buf
        buf.put(value.getBytes(DFT_CHAR_SET));
        /**
         * 2.切换buffer从写模式到读模式，准备读取buf到文件
         * buf是读写双向的，读和写的下标明显是不同的
         * 默认为写模式，在需要读时，必须切换为读模式
         */
        buf.flip();
        // 3.通道从缓冲区读数据(到文件)
        fileChannel.write(buf);
        /**
         * 4.清空buffer
         * 在下一次读写之前，必须清空buffer
         * 一是由于flip已经把buffer的position切换为0，下次会从0开始写，如果不清除buffer，下次几乎一定会读入尾部的脏数据
         * 二是防止内存溢出，buffer在空间不足以写入新的数据时会抛出内存溢出错误
         */
        buf.clear();
        /**
         * 5.指定通道的当前位置为文件末尾
         * 默认当前位置为0，即同一个通道，每次都会从下标0开始写入，即从头开始覆盖已经写入的字节
         * 此处需要追加写入，需指定通道位置为文件末尾
         */
        fileChannel.position(file.length());
    }

}
