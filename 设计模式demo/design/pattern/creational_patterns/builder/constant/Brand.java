package design.pattern.creational_patterns.builder.constant;

/**
 * @author liuwei
 * @date 2019-07-27 16:19:50
 * @desc 品牌枚举类
 */
public enum Brand {
	//饮料
	Coke("可口可乐"),Pesi("百事可乐"),Fanta("芬达"),Sprite("雪碧"),
	Orange("橙汁"),Lemon("柠檬汁"),Apple("苹果汁"),Grape("葡萄汁"),
	//汉堡
	ChickenHamburg("香辣鸡腿堡"),BeefHamburg("牛肉堡");
	
	private String value;
	
	Brand(String value) {
		this.value= value;
	}
	
	public String value() {
		return value;
	}
}
