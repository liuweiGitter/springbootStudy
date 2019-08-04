package design.pattern.behavioral_patterns.mediator;

import design.pattern.behavioral_patterns.mediator.group.Group;
import design.pattern.behavioral_patterns.mediator.user.User;

/**
 * @author liuwei
 * @date 2019-08-03 10:28:04
 * @desc 中介者模式demo
 */
public class MediatorPattern {
	
	public static void main(String[] args) {
		//通信实体：两个对象或者对象和群
		User user1 = new User(1234567,"liuwei");
		User user2 = new User(1244567);
		Group group = new Group(4567);
		String msg = "Hello, I'm liuwei!";
		//通信调用：通信行为属于用户，但逻辑实现在通信平台
		user1.sendMsg(msg, group);
		user1.sendMsg(msg, user2);
	}
	
}
