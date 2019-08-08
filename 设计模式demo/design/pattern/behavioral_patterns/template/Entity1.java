package design.pattern.behavioral_patterns.template;

/**
 * @author liuwei
 * @date 2019-08-08 22:34:42
 * @desc 子类1
 */
public class Entity1 extends AbstractEntity {

	@Override
	void start() {
		System.out.println("Entity1启动");
	}

	@Override
	void firstStep() {
		System.out.println("Entity1第一步");
	}

	@Override
	void secondStep() {
		System.out.println("Entity1第二步");
	}

	@Override
	void thirdStep() {
		System.out.println("Entity1第三步");
	}

	@Override
	void end() {
		System.out.println("Entity1结束");
	}

}
