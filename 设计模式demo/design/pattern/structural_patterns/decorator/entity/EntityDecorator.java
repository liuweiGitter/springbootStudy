package design.pattern.structural_patterns.decorator.entity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-29 23:15:04
 * @desc 装饰器类
 */
@Slf4j
public class EntityDecorator {
	private Entity entity;
	
	public EntityDecorator(Entity entity) {
		this.entity = entity;
	}
	
	public void method() {
		log.info("...我来了");
		entity.method();
		log.info("我走了...");
	}
}
