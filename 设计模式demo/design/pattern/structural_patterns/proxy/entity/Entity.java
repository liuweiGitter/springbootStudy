package design.pattern.structural_patterns.proxy.entity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-30 21:24:09
 * @desc 原对象
 * 本对象提供了一些安全性相关的重要功能，比如金钱交易、内网和专用网访问、保密数据读写等
 * 这些功能并没有在对象中进行调用的控制，不能直接对终端用户提供接口，需要通过一个中间层进行访问控制
 * 本对象还提供了一系列复杂逻辑和接口的功能，比如远程调用、网络交互、操作系统交互等
 * 这些功能集合即使只是调用也需要理解深层逻辑，对使用者来说并不友好，需要通过一个中间层简化功能调用
 * 本对象创建时非常耗时，比如进行了网络交互和文件读写等
 * 这种特性使得对所有人都开放创建对象权限后，系统运行效率降低，需要通过一个中间层集中对象资源
 * 
 * 因为以上种种原因，外部不能直接访问本对象而添加的中间层，即为本对象的代理层
 * 代理层不同于适配器层，后者是为了扩展同性质的功能
 * 代理层不同于装饰器层，后者是为了增强不同性质的功能
 * 代理层是为了实现访问控制
 * 除了增加了代理逻辑外，代理层带来的最大问题是，由于多了一层逻辑，造成的访问效率降低
 */
@Slf4j
public class Entity {
	//安全性功能
	public void drawMoney() {
		log.info("我在取钱！");
	}
	
	public void loginVpn() {
		log.info("我在登陆VPN！");
	}
	
	public void updateDb() {
		log.info("我在修改数据库！");
	}
	
	//复杂逻辑和接口的功能
	public void createSocket() {
		log.info(">>>请求开始");
		log.info("创建socket对象！");
	}
	
	public void requestToRemote() {
		log.info("向远程服务器发起请求！");
	}
	
	public void responseFromRemote() {
		log.info("从远程服务器接收响应！");
	}
	
	public void endSocket() {
		log.info("断开socket连接！");
	}
	
	public void parseResponseData() {
		log.info("解析响应数据！");
		log.info(">>>请求结束");
	}
	
	
}
