package netty.nio.java.netty.constant;

/**
 * @author liuwei
 * @date 2019-08-14 21:49:00
 * @desc 操作系统相关常量
 * Netty服务端和客户端都会引用一些系统常量，如系统行分隔符等
 */
public class SystemConstant {
	//行分隔符
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
