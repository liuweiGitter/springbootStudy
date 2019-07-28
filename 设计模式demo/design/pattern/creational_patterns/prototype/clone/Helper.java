package design.pattern.creational_patterns.prototype.clone;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-07-28 00:39:39
 * @desc 辅助对象
 */
@Data
public class Helper implements Cloneable {
	private String name;
	private int age;
	
	@Override
	public Object clone() {
		try {
			//深度克隆
			Helper clone = (Helper) super.clone();
			clone.name = new String(name);
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
