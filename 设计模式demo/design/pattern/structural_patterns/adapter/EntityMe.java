package design.pattern.structural_patterns.adapter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-28 20:05:05
 * @desc 扩展了旧对象功能的主体类
 */
@Slf4j
public class EntityMe {
	
	//引入适配器对象，使用适配器对象调用旧对象的方法
	private Adapter adapter = new Adapter();
	
	//扩展旧对象的methodA方法
	public void methodA(String methodName) {
		if ("local".equals(methodName)) {
			methodA();
		} else if ("adapter".equals(methodName)) {
			adapter.methodA();
		} else {
			throw new NoSuchMethodError("方法不存在！");
		}
	}
	
	public void methodA() {
		log.info(">>>调用EntityMe的方法methodA");
	}
	
	//扩展旧对象的methodB方法
	public void methodB(String methodName) {
		if ("local".equals(methodName)) {
			methodB();
		} else if ("adapter".equals(methodName)) {
			adapter.methodB();
		} else {
			throw new NoSuchMethodError("方法不存在！");
		}
	}
	
	public void methodB() {
		log.info(">>>调用EntityMe的方法methodB");
	}
}
