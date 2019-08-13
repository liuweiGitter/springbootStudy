package netty.nio.java.netty.server;

/**
 * @author liuwei
 * @date 2019-08-13 23:35:16
 * @desc 服务端启动线程
 */
public class ServerStartThread {
	
	public static void main(String[] args) {
		int port = 2019;
        new NettyServer(port);
	}
}
