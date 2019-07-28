package design.pattern.structural_patterns.filter.filter;

import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-28 22:17:00
 * @desc 过滤器A：过滤属性propertyA
 */
public class FilterA extends FilterAbstractClass{

	@Override
	public List<Entity> filter(List<Entity> entitys, boolean isFilter) {
		for (int i = 0; i < entitys.size();i++) {
			//正向过滤条件==isFilter
			if (!"A".equals(entitys.get(i).getPropertyA())==isFilter) {
				entitys.remove(i);
				i--;
			}
		}
		return entitys;
	}

}
