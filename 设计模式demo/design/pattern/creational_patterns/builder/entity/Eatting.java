package design.pattern.creational_patterns.builder.entity;

/**
 * @author liuwei
 * @date 2019-07-27 17:04:46
 * @desc 
 */
public abstract class Eatting {
	protected String brand;
	protected String cupSize;
	protected float price;
	protected int num;
	protected Float discount;
	
	protected float getSubtotal() {
		return price*num*(discount==null?1:discount/10);
	}
	public void setValues(String brand,float price,int num,Float discount,String cupSize) {
		this.brand = brand;
		this.price = price;
		this.num = num;
		this.discount = discount;
		this.cupSize = cupSize;
	}
	public abstract String showMsg();
}
