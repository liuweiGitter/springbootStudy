package design.pattern.behavioral_patterns.memento;

/**
 * @author liuwei
 * @date 2019-08-05 00:26:33
 * @desc 备忘录模式demo
 */
public class MementoPattern {

	public static void main(String[] args) {
		// 实体类
		Entity entity = new Entity();
		// 备忘录管理类
		MementoManager manager = new MementoManager();
		// 实体类赋值和状态保存
		entity.setState("liu");
		manager.add(entity.saveStateToMemento());
		sleep(777);
		entity.setState("wei");
		manager.add(entity.saveStateToMemento());
		sleep(888);
		entity.setState("nan");
		manager.add(entity.saveStateToMemento());
		sleep(999);
		entity.setState("jing");
		manager.add(entity.saveStateToMemento());
		sleep(123);
		entity.setState("2019");
		manager.add(entity.saveStateToMemento());
		sleep(234);
		entity.setState("0806");
		manager.add(entity.saveStateToMemento());
		sleep(345);
		entity.setState("good");
		manager.add(entity.saveStateToMemento());
		entity.setState("night");
		// 查询历史状态
		println(manager.getFirst());
		println(manager.getLast());
		manager.showTimes();
		// 查询当前状态
		println(entity.getState());
		// 恢复到历史状态
		entity.setState(manager.getLast().getState());
		// 查询当前状态
		println(entity.getState());
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void println(Object obj) {
		System.out.println(obj);
	}
}
