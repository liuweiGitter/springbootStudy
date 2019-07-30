package design.pattern.structural_patterns.flyweight.entity;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-07-30 20:27:03
 * @desc 实体类
 * 该类的对象会被频繁使用，这些对象可能属性值各异
 * 但并不希望每次使用都在内存中创建新的对象(太低效和占内存)
 * 而且实际也并不需要每次都创建新对象(并非耗时操作，且并发量不大)
 * 
 * 如果使用单例模式，只需创建一次对象，可以最大程度地节省内存，但效率却不是最高的
 * 单例对象每次使用时都需要对属性进行按需重新赋值，赋值是花费时间的，而且并发时所有后续调用都得同步等待
 * 
 * 所幸，对象的一些属性的取值是可枚举的
 * 折中的做法是，按照某个可枚举的标志性属性在内存中创建多个对象，在使用时根据枚举取值获取相应的对象
 * 获取到对象后，根据需要对其它属性赋值后可使用
 * 如此，增加了一些内存使用，但减少了赋值操作和并发等待，提高了使用效率
 * 
 * 使用一个专门的工厂类来创建、维持和访问多例对象
 * 由于多个对象的区别仅在于某个属性值，因此可以使用HashMap<#,Entity>来维持这些对象
 * 其中，#为可枚举标志属性的数据类型
 */
@Data
public class Entity {
	private String type;//可枚举标志属性
	private String value1;
	private String value2;
	
	public Entity(String type) {
		this.type = type;
	}
	
}
