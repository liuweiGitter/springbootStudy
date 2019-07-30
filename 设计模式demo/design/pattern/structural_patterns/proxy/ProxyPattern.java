package design.pattern.structural_patterns.proxy;

/**
 * @author liuwei
 * @date 2019-07-30 21:17:26
 * @desc 代理模式demo
 * 本demo演示静态代理
 * 静态代理需要对每一个被代理的类编写代理类，且即使代理的每一个方法的切面逻辑都一样，也要分别编写重复的切面逻辑
 * 如果项目中需要对多个类和方法进行代理，最好使用动态代理
 * 关于动态代理，参见相关文档
 */
public class ProxyPattern {
	
	public static void main(String[] args) {
		EntityProxy proxy = new EntityProxy();
		String role = "admin";
		proxy.drawMoney(role);
		proxy.loginVpn(role);
		proxy.updateDb(role);
		proxy.rpcCall();
	}
}
