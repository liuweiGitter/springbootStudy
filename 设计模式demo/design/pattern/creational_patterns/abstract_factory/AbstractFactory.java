package design.pattern.creational_patterns.abstract_factory;

import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceA;
import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceB;

/**
 * @author liuwei
 * @date 2019-07-27 12:39:08
 * @desc 抽象工厂接口
 */
public interface AbstractFactory {
	String getDesc();
	EntityInterfaceA getInstanceA(Class<? extends EntityInterfaceA> clz);
	EntityInterfaceB getInstanceB(Class<? extends EntityInterfaceB> clz);
}
