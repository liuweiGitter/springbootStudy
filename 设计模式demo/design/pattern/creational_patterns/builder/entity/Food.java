package design.pattern.creational_patterns.builder.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuwei
 * @date 2019-07-27 17:03:19
 * @desc 
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Food extends Eatting{
	@Override
	public String showMsg() {
		return "brand:"+brand+",single price:￥"+price+",num:"+num
				+",discount:"+(discount==null?"-":discount)
				+",subPrice:￥"+getSubtotal();
	}
	public Food() {
		
	}
}
