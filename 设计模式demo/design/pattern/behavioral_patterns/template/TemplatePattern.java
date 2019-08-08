package design.pattern.behavioral_patterns.template;

/**
 * @author liuwei
 * @date 2019-08-08 22:12:45
 * @desc 模板模式demo
 */
public class TemplatePattern {

	public static void main(String[] args) {
		AbstractEntity abstractEntity= new Entity1();
		abstractEntity.template();
		
		abstractEntity = new Entity2();
		abstractEntity.template();
	}
	
}
