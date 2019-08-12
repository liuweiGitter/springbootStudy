package netty.nio.java.nio.selector.socketserver;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author liuwei
 * @date 2019-08-12 00:54:23
 * @desc socket服务端
 */
@Slf4j
public class SocketServer {

	//选择器
	private Selector selector;

	// 本地监听端口
	private int listenPort = 10086;
	
	public SocketServer(){
		try {
			initialize();
			new SelectorThread(selector);
		} catch (IOException e) {
			log.info(""+e);
		}
	}

	public SocketServer(int port){
		this.listenPort = port;
		try {
			initialize();
			// 启动选择器线程：在选择器线程中处理客户端通道的IO
			new SelectorThread(selector);
		} catch (IOException e) {
			log.info(""+e);
		}
	}

	private void initialize() throws IOException {
		// 创建选择器
		selector = Selector.open();

		// 创建服务监听通道
		ServerSocketChannel listenerChannel = ServerSocketChannel.open();

		// 绑定本地监听端口
		listenerChannel.socket().bind(new InetSocketAddress(listenPort));

		// 设置通道为非阻塞模式(只有非阻塞信道才可以注册选择器)
		listenerChannel.configureBlocking(false);

		/**
		 * 注册通道的Accept事件(用以监听客户端请求)到选择器
		 * when server-socket channel is ready to accept another connection, or has an error pending
		 * then it will add OP_ACCEPT to the key's ready set
		 * and add the key to its selected-key set
		 */
		listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

	}
}
