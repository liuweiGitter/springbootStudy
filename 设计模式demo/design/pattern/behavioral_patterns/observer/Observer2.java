package design.pattern.behavioral_patterns.observer;

/**
 * @author liuwei
 * @date 2019-08-07 22:07:40
 * @desc 观察者2
 */
public class Observer2 extends Observer {

	// 观察者构造，在创建观察者的同时，就指定被观察者对象，并在被观察者对象中添加本观察者
	public Observer2(Entity entity) {
		this.entity = entity;
		this.entity.attach(this);
	}

	@Override
	protected void update() {
		System.out.println("我是观察者2号，收到被观察者状态已变化通知！被观察者当前状态为：" + entity.getState());
	}

}
