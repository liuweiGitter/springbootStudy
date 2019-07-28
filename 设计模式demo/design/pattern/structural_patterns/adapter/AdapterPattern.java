package design.pattern.structural_patterns.adapter;

/**
 * @author liuwei
 * @date 2019-07-28 18:25:57
 * @desc 适配器模式demo
 * 新对象扩展旧对象方法功能时，使用适配器隔离新旧对象的直接依赖
 */
public class AdapterPattern {
	
	public static void main(String[] args) {
		EntityMe me = new EntityMe();
		me.methodA("local");
		me.methodA("adapter");
		me.methodB("local");
		me.methodB("adapter");
	}
}
