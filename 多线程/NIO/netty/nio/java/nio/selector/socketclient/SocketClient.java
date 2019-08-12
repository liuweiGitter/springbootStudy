package netty.nio.java.nio.selector.socketclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
/**
 * @author liuwei
 * @date 2019-08-11 23:31:07
 * @desc socket客户端
 */
public class SocketClient {
	// 通道选择器
	private Selector selector;
	// 客户端通道
	SocketChannel socketChannel;
	// 服务器Ip地址
	private String serverIp;
	// 服务器的监听端口
	private int serverPort;

	public SocketClient(String serverIp,int serverPort)throws IOException{
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		initialize();
	}

	/**
	 * 通道和选择器初始化
	 */
	private void initialize() throws IOException {
		// 打开监听信道并设置为非阻塞模式
		socketChannel = SocketChannel.open(new InetSocketAddress(serverIp, serverPort));
		socketChannel.configureBlocking(false);

		// 打开并选择器并注册信道：注册读事件，读取来自服务器的响应
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ);

		// 启动选择器线程：在选择器线程中处理客户端通道的IO
		new SelectorThread(selector);
	}

	/**
	 * 发送字符串到服务器
	 */
	public void sendMsg(String message) throws IOException{
		ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));
		socketChannel.write(writeBuffer);
	}

	/**
	 * 关闭客户端通道
	 */
	public void closeChannel() throws IOException {
		socketChannel.close();
	}

}

