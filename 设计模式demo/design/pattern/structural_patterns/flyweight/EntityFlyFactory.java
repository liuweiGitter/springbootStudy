package design.pattern.structural_patterns.flyweight;

import java.util.HashMap;
import java.util.Map;

import design.pattern.structural_patterns.flyweight.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-30 21:02:20
 * @desc 享元工厂类
 */
public class EntityFlyFactory {
	private static Map<String,Entity> entityMap = new HashMap<>();
	
	public static Entity getEntity(String type) {
		if (!entityMap.containsKey(type)) {
			entityMap.put(type, new Entity(type));
		}
		return entityMap.get(type);
	}
}
