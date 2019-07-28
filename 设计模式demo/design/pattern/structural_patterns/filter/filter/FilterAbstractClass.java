package design.pattern.structural_patterns.filter.filter;

import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-28 23:31:41
 * @desc 过滤器的抽象类，过滤某一组对象的过滤方法的定义
 */
public abstract class FilterAbstractClass{

	//正向过滤
	public List<Entity> filter(List<Entity> entitys) {
		return filter(entitys,true);
	}

	//反转过滤
	public List<Entity> filterReverse(List<Entity> entitys) {
		return filter(entitys,false);
	}

	public abstract List<Entity> filter(List<Entity> entitys, boolean isFilter);

}
