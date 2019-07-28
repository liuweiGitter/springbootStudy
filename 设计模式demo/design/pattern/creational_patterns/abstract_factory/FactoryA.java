package design.pattern.creational_patterns.abstract_factory;

import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceA;
import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceB;

/**
 * @author liuwei
 * @date 2019-07-27 12:38:32
 * @desc 工厂类A
 */
public class FactoryA implements AbstractFactory{

	@Override
	public String getDesc() {
		return "工厂类A";
	}
	
	public EntityInterfaceA getInstanceA(Class<? extends EntityInterfaceA> clz) {
		try {
			return clz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public EntityInterfaceB getInstanceB(Class<? extends EntityInterfaceB> clz) {
		return null;
	}
	
}
