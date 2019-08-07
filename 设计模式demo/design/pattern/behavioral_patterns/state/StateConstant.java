package design.pattern.behavioral_patterns.state;

/**
 * @author liuwei
 * @date 2019-08-08 00:11:11
 * @desc 状态常量类
 * 定义了所有的状态对象
 */
public class StateConstant {
	public static final State RUNNING = new RunningState();
	public static final State SLEEPING = new SleepingState();
	public static final State STOPING = new StopingState();
}
