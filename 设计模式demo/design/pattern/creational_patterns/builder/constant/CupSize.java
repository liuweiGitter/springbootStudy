package design.pattern.creational_patterns.builder.constant;

/**
 * @author liuwei
 * @date 2019-07-27 16:19:50
 * @desc 杯量枚举类
 */
public enum CupSize {
	
	Big("大杯"),Middle("中杯"),Small("小杯");
	
	private String value;
	
	CupSize(String value) {
		this.value= value;
	}
	
	public String value() {
		return value;
	}
}
