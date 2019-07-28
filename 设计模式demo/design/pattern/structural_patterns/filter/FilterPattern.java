package design.pattern.structural_patterns.filter;

import java.util.ArrayList;
import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;
import design.pattern.structural_patterns.filter.filter.FilterA;
import design.pattern.structural_patterns.filter.filter.FilterAnd;
import design.pattern.structural_patterns.filter.filter.FilterB;
import design.pattern.structural_patterns.filter.filter.FilterC;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-28 22:04:39
 * @desc 过滤器模式demo
 * 输入一组对象列表，通过一系列过滤条件，输出筛选过的对象列表
 */
@Slf4j
public class FilterPattern {
	
	public static void main(String[] args) {
		List<Entity> entitys = new ArrayList<Entity>();
		entitys.add(new Entity("A", "B", "C", "D"));
		entitys.add(new Entity("B", "B", "C", "D"));
		entitys.add(new Entity("C", "C", "C", "D"));
		entitys.add(new Entity("D", "D", "D", "D"));
		entitys.add(new Entity("A1", "B1", "C1", "D1"));
		//1.单过滤器过滤
		FilterA filterA = new FilterA();
		//printResult(filterA.filter(entitys));
		//printResult(filterA.filterReverse(entitys));
		//2.双过滤器过滤
		FilterB filterB = new FilterB();
		FilterAnd filterAAndB = new FilterAnd(filterA, filterB);
		//printResult(filterAAndB.reverseThenFilter(entitys));
		//3.多过滤器过滤
		FilterC filterC = new FilterC();
		printResult(filterC.filterReverse(
				filterAAndB.filterReverse(entitys)));
	}
	
	private static void printResult(List<Entity> entitys) {
		if (null==entitys || entitys.size()==0) {
			log.info("\n----过滤结果为空----");
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (Entity entity : entitys) {
			sb.append("\n"+entity.toString());
		}
		log.info(sb.toString());
	}
}
