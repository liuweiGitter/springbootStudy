package design.pattern.behavioral_patterns.strategy.strategy;

/**
 * @author liuwei
 * @date 2019-08-08 21:32:57
 * @desc 加法操作(算法即策略)
 */
public class OperationAdd implements Strategy {

	@Override
	public int bothOperation(int num1, int num2) {
		return num1+num2;
	}

	@Override
	public String strategyName() {
		return "加法策略";
	}

}
