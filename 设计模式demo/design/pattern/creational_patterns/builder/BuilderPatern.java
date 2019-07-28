package design.pattern.creational_patterns.builder;

import design.pattern.creational_patterns.builder.constant.HotDegree;
import design.pattern.creational_patterns.builder.entity.Meal;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-27 20:25:28
 * @desc 创建者模式demo
 */
@Slf4j
public class BuilderPatern {
	
	public static void main(String[] args) {
		Builder builder = new Builder();
		Meal meal1 = builder.singleHamburgCokeMeal(HotDegree.Cold.value());
		log.info("\n"+meal1.getMsg());
		Meal meal2 = builder.doubleHamburgCokeMeal(HotDegree.Cold.value());
		log.info("\n"+meal2.getMsg());
	}
}
