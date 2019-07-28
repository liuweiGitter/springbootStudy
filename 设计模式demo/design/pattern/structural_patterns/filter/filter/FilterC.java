package design.pattern.structural_patterns.filter.filter;

import java.util.ArrayList;
import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-28 22:17:00
 * @desc 过滤器C：过滤属性propertyC
 */
public class FilterC extends FilterAbstractClass{

	@Override
	public List<Entity> filter(List<Entity> entitys, boolean isFilter) {
		List<Entity> filterResult= new ArrayList<Entity>();
		for (Entity entity : entitys) {
			//正向过滤条件==isFilter
			if ("C".equals(entity.getPropertyC())==isFilter) {
				filterResult.add(entity);
			}
		}
		return filterResult;
	}

}
