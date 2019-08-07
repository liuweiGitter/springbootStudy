package design.pattern.behavioral_patterns.state;

/**
 * @author liuwei
 * @date 2019-08-07 23:25:18
 * @desc 休眠态
 */
public class SleepingState implements State {

	@Override
	public void longPress(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("长按唤醒");
		System.out.println("已唤醒");
		computer.setState(StateConstant.RUNNING);
	}

	@Override
	public void shortPress(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("短按唤醒");
		System.out.println("已唤醒");
		computer.setState(StateConstant.RUNNING);
	}

	@Override
	public void openCover(Computer computer) {
		// doing nothing
	}

	@Override
	public void closeCover(Computer computer) {
		// doing nothing
	}

	@Override
	public String currentState() {
		return "休眠状态";
	}

}
