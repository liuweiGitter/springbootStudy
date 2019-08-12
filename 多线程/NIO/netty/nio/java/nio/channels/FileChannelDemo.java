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

}
