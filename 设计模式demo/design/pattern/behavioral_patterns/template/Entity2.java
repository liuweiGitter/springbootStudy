package design.pattern.behavioral_patterns.template;

/**
 * @author liuwei
 * @date 2019-08-08 22:34:42
 * @desc 子类2
 */
public class Entity2 extends AbstractEntity {

	@Override
	void start() {
		System.out.println("Entity2启动");
	}

	@Override
	void firstStep() {
		System.out.println("Entity2第一步");
	}

	@Override
	void secondStep() {
		System.out.println("Entity2第二步");
	}

	@Override
	void thirdStep() {
		System.out.println("Entity2第三步");
	}

	@Override
	void end() {
		System.out.println("Entity2结束");
	}

}
