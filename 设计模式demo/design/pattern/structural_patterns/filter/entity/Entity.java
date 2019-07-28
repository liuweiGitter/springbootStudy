package design.pattern.structural_patterns.filter.entity;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-07-28 22:21:54
 * @desc 需要被过滤的列表的对象元素：其中的某些属性需要被过滤
 */
@Data
public class Entity {
	
	private String propertyA;
	private String propertyB;
	private String propertyC;
	private String propertyD;
	
	public Entity(String propertyA, String propertyB, String propertyC, String propertyD) {
		super();
		this.propertyA = propertyA;
		this.propertyB = propertyB;
		this.propertyC = propertyC;
		this.propertyD = propertyD;
	}

	@Override
	public String toString() {
		return "Entity [propertyA=" + propertyA + ", propertyB=" + propertyB + ", propertyC=" + propertyC
				+ ", propertyD=" + propertyD + "]";
	}
	
}
