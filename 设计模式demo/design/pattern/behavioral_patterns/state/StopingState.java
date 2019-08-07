package design.pattern.behavioral_patterns.state;

/**
 * @author liuwei
 * @date 2019-08-07 23:25:18
 * @desc 关机态
 */
public class StopingState implements State {

	@Override
	public void longPress(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("长按开机");
		System.out.println("已开机");
		computer.setState(StateConstant.RUNNING);
	}

	@Override
	public void shortPress(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("短按开机");
		System.out.println("已开机");
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
		return "关机状态";
	}

}
