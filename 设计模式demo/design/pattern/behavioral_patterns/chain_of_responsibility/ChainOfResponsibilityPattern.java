package design.pattern.behavioral_patterns.chain_of_responsibility;

import java.util.HashSet;
import java.util.Set;

import design.pattern.behavioral_patterns.chain_of_responsibility.entity.AbstractRes;

/**
 * @author liuwei
 * @date 2019-07-31 00:36:28
 * @desc 责任链模式demo
 */
public class ChainOfResponsibilityPattern {
	
	public static void main(String[] args) {
		//获取责任链入口对象
		AbstractRes entrance = ChainEntrance.getChainNode();
		//调用前参数准备
		Set<String> doFlags = new HashSet<String>();
		doFlags.add("doCommon1");
		doFlags.add("doCommon2");
		String logicParam = "业务逻辑参数";
		//业务逻辑调用
		entrance.doLogic1(doFlags, logicParam);
		entrance.doLogic2(doFlags, logicParam);
	}
	
}
