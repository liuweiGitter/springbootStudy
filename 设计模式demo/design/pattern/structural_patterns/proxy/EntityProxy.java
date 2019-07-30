package design.pattern.structural_patterns.proxy;

import design.pattern.structural_patterns.proxy.entity.Entity;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-30 22:08:17
 * @desc 代理类
 */
@Slf4j
public class EntityProxy {
	private Entity entity;
	
	public EntityProxy() {
		this.entity = new Entity();
	}
	
	public void drawMoney(String role) {
		log.info("你的用户角色是："+role);
		if ("admin".equals(role)) {
			entity.drawMoney();
		} else {
			log.info("未经授权，不得操作！");
		}
	}
	
	public void loginVpn(String role) {
		log.info("你的用户角色是："+role);
		if ("admin".equals(role)) {
			entity.loginVpn();
		} else {
			log.info("未经授权，不得操作！");
		}
	}
	
	public void updateDb(String role) {
		log.info("你的用户角色是："+role);
		if ("admin".equals(role)) {
			entity.updateDb();
		} else {
			log.info("未经授权，不得操作！");
		}
	}
	
	public void rpcCall() {
		log.info("---我是代理，我在帮你发起rpc调用");
		entity.createSocket();
		entity.requestToRemote();
		entity.responseFromRemote();
		entity.endSocket();
		entity.parseResponseData();
		log.info("---我是代理，rpc调用结束");
	}
	
}
