package design.pattern.creational_patterns.builder.constant;

/**
 * @author liuwei
 * @date 2019-07-27 16:19:50
 * @desc 甜度枚举类
 */
public enum SugarDegree {
	
	Eight("八分"),Seven("七分"),Six("六分"),Five("五分"),
	Four("四分"),Three("三分"),Two("二分"),One("一分"),Zero("无");
	
	private String value;
	
	SugarDegree(String value) {
		this.value= value;
	}
	
	public String value() {
		return value;
	}
}
