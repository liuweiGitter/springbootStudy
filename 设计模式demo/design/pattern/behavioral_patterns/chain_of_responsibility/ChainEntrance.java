package design.pattern.behavioral_patterns.chain_of_responsibility;

import java.util.HashSet;
import java.util.Set;

import design.pattern.behavioral_patterns.chain_of_responsibility.entity.AbstractRes;
import design.pattern.behavioral_patterns.chain_of_responsibility.entity.LineWorkerA;
import design.pattern.behavioral_patterns.chain_of_responsibility.entity.LineWorkerB;
import design.pattern.behavioral_patterns.chain_of_responsibility.entity.LineWorkerC;

/**
 * @author liuwei
 * @date 2019-07-31 23:59:45
 * @desc 责任链入口类 定义链顺序
 */
public class ChainEntrance {

	private static AbstractRes entrance;
	
	public static AbstractRes getChainNode() {
		if (null==entrance) {
			Set<String> aFlags = new HashSet<String>();
			aFlags.add("doA");
			aFlags.add("doCommon1");
			aFlags.add("doCommon2");

			Set<String> bFlags = new HashSet<String>();
			bFlags.add("doB");

			Set<String> cFlags = new HashSet<String>();
			cFlags.add("doC");
			cFlags.add("doCommon2");

			AbstractRes lineWorkerA = new LineWorkerA(aFlags);
			AbstractRes lineWorkerB = new LineWorkerB(bFlags);
			AbstractRes lineWorkerC = new LineWorkerC(cFlags);

			lineWorkerA.setNextChainNode(lineWorkerB);
			lineWorkerB.setNextChainNode(lineWorkerC);

			entrance=lineWorkerA;
		}
		return entrance;
	}
}
