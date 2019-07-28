package design.pattern.structural_patterns.filter.filter;

import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-28 23:05:33
 * @desc And交集过滤器
 */
public class FilterAnd extends FilterMultiAbstractClass {

	private FilterAbstractClass filter1;
	private FilterAbstractClass filter2;

	public FilterAnd(FilterAbstractClass filter1, FilterAbstractClass filter2) {
		this.filter1 = filter1;
		this.filter2 = filter2;
	}

	@Override
	List<Entity> filter(List<Entity> entitys, boolean isFilter1, boolean isFilter2) {
		return filter2.filter(filter1.filter(entitys,isFilter1), isFilter2);
	}

}
