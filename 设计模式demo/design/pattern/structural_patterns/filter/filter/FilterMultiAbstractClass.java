package design.pattern.structural_patterns.filter.filter;

import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-29 00:10:40
 * @desc 两个过滤器接力过滤的抽象类
 */
public abstract class FilterMultiAbstractClass {

	// 正向过滤+正向过滤
	public List<Entity> filter(List<Entity> entitys) {
		return filter(entitys, true, true);
	}

	// 反向过滤+反向过滤
	public List<Entity> filterReverse(List<Entity> entitys) {
		return filter(entitys, false, false);
	}

	// 正向过滤+反向过滤
	public List<Entity> filterThenReverse(List<Entity> entitys) {
		return filter(entitys, true, false);
	}

	// 反向过滤+正向过滤
	public List<Entity> reverseThenFilter(List<Entity> entitys) {
		return filter(entitys, false, true);
	}

	abstract List<Entity> filter(List<Entity> entitys, boolean isFilter1, boolean isFilter2);
}
