package design.pattern.creational_patterns.builder.constant;

/**
 * @author liuwei
 * @date 2019-07-27 16:19:50
 * @desc 热量枚举类
 */
public enum HotDegree {
	
	Hot("热"),Normal("常温"),Cold("冷");
	
	private String value;
	
	HotDegree(String value) {
		this.value= value;
	}
	
	public String value() {
		return value;
	}
}
