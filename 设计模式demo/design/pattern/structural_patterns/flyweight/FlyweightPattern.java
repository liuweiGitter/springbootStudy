package design.pattern.structural_patterns.flyweight;

import design.pattern.structural_patterns.flyweight.entity.Entity;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-30 15:00
 * @desc 享元模式demo
 */
@Slf4j
public class FlyweightPattern {
	
	public static void main(String[] args) {
		Entity entity1 = EntityFlyFactory.getEntity("type1");
		Entity entity2 = EntityFlyFactory.getEntity("type2");
		Entity entity3 = EntityFlyFactory.getEntity("type1");
		log.info("entity1==entity2："+(entity1==entity2));
		//entity1和entity3共享同一个对象
		log.info("entity1==entity3："+(entity1==entity3));
	}
}
