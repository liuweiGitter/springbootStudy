package design.pattern.structural_patterns.adapter;

/**
 * @author liuwei
 * @date 2019-07-28 20:19:55
 * @desc 适配器类
 * 适配器类将旧对象创建和方法调用的所有逻辑独立于新对象
 * 隔离了新旧对象，增强了代码的可读性，更利于后续功能的扩展
 * 尤其是当一个对象扩展了多个旧对象时，适配器的隔离性表现的更突出
 * 除此之外，独立的适配器使得适配功能可以被复用，在另一个新对象需要同样的适配时，直接引入即可
 */
public class Adapter {

	//被扩展功能的旧对象
	private EntityIt entityIt = new EntityIt();

	//被扩展功能的旧方法
	public void methodA() {
		entityIt.methodA();
	}

	//被扩展功能的旧方法
	public void methodB() {
		entityIt.methodB();
	}
}
