package design.pattern.creational_patterns.factory;

import design.pattern.creational_patterns.factory.entity.EntityA;
import design.pattern.creational_patterns.factory.entity.EntityB;
import design.pattern.creational_patterns.factory.entity.EntityInterface;

/**
 * @author liuwei
 * @date 2019-07-27 11:11:57
 * @desc 工厂类
 */
public class Factory {
	
	/**
	 * 工厂方法：根据传参多态获取目标对象
	 */
	
	//字符串传参：代码冗余，不利扩展，传参易出错，不建议
	public static EntityInterface getInstance(String objName) {
		if ("A".equals(objName)) {
			return new EntityA();
		} else if ("B".equals(objName)) {
			return new EntityB();
		} else {
			return null;
		}
	}
	
	//字节码对象传参：推荐
	public static EntityInterface getInstance(Class<? extends EntityInterface> clz) {
		try {
			return clz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
