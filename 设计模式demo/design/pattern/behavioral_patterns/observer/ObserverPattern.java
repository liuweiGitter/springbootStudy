package design.pattern.behavioral_patterns.observer;

/**
 * @author liuwei
 * @date 2019-08-06 00:20:26
 * @desc 观察者模式demo
 */
public class ObserverPattern {
	
	public static void main(String[] args) {
		//创建被观察者对象
		Entity entity = new Entity();
		//创建一组观察者对象
		new Observer1(entity);
		new Observer2(entity);
		new Observer3(entity);
		//修改被观察者状态
		entity.setState(1);
		entity.setState(2);
	}
	
}
