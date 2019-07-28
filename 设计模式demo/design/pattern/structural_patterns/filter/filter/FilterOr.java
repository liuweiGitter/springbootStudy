package design.pattern.structural_patterns.filter.filter;

import java.util.List;

import design.pattern.structural_patterns.filter.entity.Entity;

/**
 * @author liuwei
 * @date 2019-07-28 23:05:33
 * @desc Or并集过滤器
 */
public class FilterOr extends FilterMultiAbstractClass {

   private FilterAbstractClass filter1;
   private FilterAbstractClass filter2;
 
   public FilterOr(FilterAbstractClass filter1, FilterAbstractClass filter2) {
      this.filter1 = filter1;
      this.filter2 = filter2; 
   }

	@Override
	List<Entity> filter(List<Entity> entitys, boolean isFilter1, boolean isFilter2) {
		List<Entity> filterResult1= filter1.filter(entitys,isFilter1);
		List<Entity> filterResult2= filter2.filter(entitys,isFilter2);
		for (Entity entity : filterResult2) {
			if (!filterResult1.contains(entity)) {
				filterResult1.add(entity);
			}
		}
		return filterResult1;
	}

}
