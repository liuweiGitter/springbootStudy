package design.pattern.behavioral_patterns.state;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-07 23:19:13
 * @desc 笔记本类
 */
@Data
public class Computer {
	//当前状态对象
	private State state;
	
	//长按开机键
	public void longPress() {
		state.longPress(this);
	}

	//短按开机键
	public void shortPress() {
		state.shortPress(this);
	}

	//打开笔记本
	public void openCover() {
		state.openCover(this);
	}

	//合上笔记本
	public void closeCover() {
		state.closeCover(this);
	}
}
