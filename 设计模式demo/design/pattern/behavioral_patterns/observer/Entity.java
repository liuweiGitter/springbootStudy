package design.pattern.behavioral_patterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 * @date 2019-08-07 21:56:38
 * @desc 被观察者类
 * 被观察者持有一组观察者对象，当本对象被观察的属性变化时，通知这些观察者
 * 观察者持有一个被观察者对象，以能够从此对象中获取自己需要的属性值
 * 观察者和被观察者互相持有
 * 观察者模式的应用场景是：
 * 当一个对象的属性变化时，依赖于该对象的多个对象需要自动得到通知并执行自己的属性更新或其它代码逻辑
 * 比如延时更新的热配置等
 */
public class Entity {
	private List<Observer> observers = new ArrayList<Observer>();
	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		//状态改变时，通知所有观察者去执行代码逻辑
		notifyAllObservers();
	}

	public void attach(Observer observer) {
		observers.add(observer);
	}

	public void notifyAllObservers() {
		//所有的观察者都去执行代码逻辑
		for (Observer observer : observers) {
			observer.update();
		}
	}
}
