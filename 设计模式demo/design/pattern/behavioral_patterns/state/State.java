package design.pattern.behavioral_patterns.state;

/**
 * @author liuwei
 * @date 2019-08-07 23:14:52
 * @desc 状态接口类
 * 定义了各种状态(关机态、开机态、休眠态)下可能的操作方法
 * 模拟笔记本的开机按键和屏幕开合的操作
 */
public interface State {
	//当前状态
	String currentState();
	//长按开机键
	void longPress(Computer computer);
	//短按开机键
	void shortPress(Computer computer);
	//打开笔记本
	void openCover(Computer computer);
	//合上笔记本
	void closeCover(Computer computer);
}
