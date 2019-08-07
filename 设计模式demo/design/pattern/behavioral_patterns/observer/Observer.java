package design.pattern.behavioral_patterns.observer;

/**
 * @author liuwei
 * @date 2019-08-07 21:59:53
 * @desc 观察者的抽象类
 * 多个观察者需要观察一个对象时，这些观察者最好继承自同一个抽象类
 * 以方便在被观察者类中使用多态持有观察者列表
 */
public abstract class Observer {

	// 持有被观察者对象，以能够从此对象中获取自己需要的属性值
	protected Entity entity;

	// 在被观察的对象状态变化时，观察者需要执行的具体代码逻辑接口
	protected abstract void update();

}
