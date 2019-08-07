package design.pattern.behavioral_patterns.state;

/**
 * @author liuwei
 * @date 2019-08-07 23:25:18
 * @desc 开机态
 */
public class RunningState implements State {

	@Override
	public void longPress(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("长按关机");
		System.out.println("正在保存文件...");
		System.out.println("已关机");
		computer.setState(StateConstant.STOPING);
	}

	@Override
	public void shortPress(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("短按休眠");
		System.out.println("已休眠");
		computer.setState(StateConstant.SLEEPING);
	}

	@Override
	public void openCover(Computer computer) {
		// doing nothing
	}

	@Override
	public void closeCover(Computer computer) {
		System.err.println("当前状态："+currentState());
		System.out.println("合盖休眠");
		System.out.println("已休眠");
		computer.setState(StateConstant.SLEEPING);
	}

	@Override
	public String currentState() {
		return "开机状态";
	}

}
