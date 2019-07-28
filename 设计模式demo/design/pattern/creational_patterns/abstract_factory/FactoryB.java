package design.pattern.creational_patterns.abstract_factory;

import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceA;
import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceB;

/**
 * @author liuwei
 * @date 2019-07-27 12:38:32
 * @desc 工厂类B
 */
public class FactoryB implements AbstractFactory{
	
	@Override
	public String getDesc() {
		return "工厂类B";
	}

	public EntityInterfaceA getInstanceA(Class<? extends EntityInterfaceA> clz) {
		return null;
	}

	@Override
	public EntityInterfaceB getInstanceB(Class<? extends EntityInterfaceB> clz) {
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
