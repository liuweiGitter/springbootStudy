package design.pattern.creational_patterns.factory;

import design.pattern.creational_patterns.factory.entity.EntityA;
import design.pattern.creational_patterns.factory.entity.EntityB;
import design.pattern.creational_patterns.factory.entity.EntityInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-27 11:07:02
 * @desc 工厂模式demo
   * 子类特别多，且需要根据条件多态创建不同的子类对象时
   * 定义一个三方类专门用以多态创建子类对象
 */
@Slf4j
public class FactoryPattern {
	
	public static void main(String[] args) {
		EntityInterface a = Factory.getInstance(EntityA.class);
		EntityInterface b = Factory.getInstance(EntityB.class);
		log.info("a.name:"+a.getClass().getName());
		log.info("b.name:"+b.getClass().getName());
	}
}
