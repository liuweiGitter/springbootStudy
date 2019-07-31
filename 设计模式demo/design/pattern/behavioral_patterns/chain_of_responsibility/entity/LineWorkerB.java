package design.pattern.behavioral_patterns.chain_of_responsibility.entity;

import java.util.Set;

/**
 * @author liuwei
 * @date 2019-07-31 21:12:56
 * @desc 责任链处理人B
 */
public class LineWorkerB extends AbstractRes {

	//构造函数：初始化本人的责任处理标记
	public LineWorkerB(Set<String> doFlags) {
		this.doFlags = doFlags;
	}
	
	@Override
	protected void realLogic(Object logicParam) {
		System.out.println("我是工人B，工号9528，盖个戳["+logicParam+"]");
	}

}
