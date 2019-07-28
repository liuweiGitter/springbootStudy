package design.pattern.creational_patterns.abstract_factory;

import design.pattern.creational_patterns.abstract_factory.entity.EntityA1;
import design.pattern.creational_patterns.abstract_factory.entity.EntityA2;
import design.pattern.creational_patterns.abstract_factory.entity.EntityB1;
import design.pattern.creational_patterns.abstract_factory.entity.EntityB2;
import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceA;
import design.pattern.creational_patterns.abstract_factory.entity.EntityInterfaceB;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-27 11:07:02
 * @desc 抽象工厂模式demo
   * 大对象由多个小对象组合而成，每一个小对象又需要用到工厂模式时
   * 对多个小对象的工厂提取出一个抽象工厂并集
 */
@Slf4j
public class AbstractFactoryPattern {
	
	public static void main(String[] args) {
		//工厂A
		AbstractFactory factoryA = new FactoryA();
		EntityInterfaceA a1 = factoryA.getInstanceA(EntityA1.class);
		EntityInterfaceA a2 = factoryA.getInstanceA(EntityA2.class);
		log.info("factoryA.desc:"+factoryA.getDesc());
		log.info("a1.name:"+a1.getClass().getSimpleName());
		log.info("a2.name:"+a2.getClass().getSimpleName());
		//工厂B
		AbstractFactory factoryB = new FactoryB();
		EntityInterfaceB b1 = factoryB.getInstanceB(EntityB1.class);
		EntityInterfaceB b2 = factoryB.getInstanceB(EntityB2.class);
		log.info("factoryB.desc:"+factoryB.getDesc());
		log.info("b1.name:"+b1.getClass().getSimpleName());
		log.info("b2.name:"+b2.getClass().getSimpleName());
	}
}
