package design.pattern.creational_patterns.builder.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuwei
 * @date 2019-07-27 16:03:34
 * @desc 
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Drink extends Eatting{
	private String sugarDegree;
	private String hotDegree;
	@Override
	public String showMsg() {
		return "brand:"+brand+",single price:￥"+price+",num:"+num
				+",discount:"+(discount==null?"-":discount)
				+",subPrice:￥"+getSubtotal()
				+"\n\t(cupSize:"+cupSize+",sugarDegree:"+sugarDegree
				+",hotDegree:"+hotDegree+")";
	}
	public Drink() {
		
	}
	public Drink(String sugarDegree, String hotDegree) {
		super();
		this.sugarDegree = sugarDegree;
		this.hotDegree = hotDegree;
	}
}
