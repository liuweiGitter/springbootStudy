package design.pattern.structural_patterns.decorator;

import design.pattern.structural_patterns.decorator.entity.Entity;
import design.pattern.structural_patterns.decorator.entity.EntityDecorator;

/**
 * @author liuwei
 * @date 2019-07-29 22:47:37
 * @desc 装饰器模式demo
 */
public class DecoratorPattern {
	
	public static void main(String[] args) {
		EntityDecorator decorator = new EntityDecorator(new Entity());
		decorator.method();
	}
	
}
