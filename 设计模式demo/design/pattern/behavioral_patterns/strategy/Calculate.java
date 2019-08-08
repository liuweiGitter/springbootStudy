package design.pattern.behavioral_patterns.strategy;

import design.pattern.behavioral_patterns.strategy.strategy.Strategy;
import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-08 21:54:11
 * @desc 计算类 持有策略对象
 */
@Data
public class Calculate {
	
	private Strategy strategy;

	public int executeStrategy(int num1, int num2) {
		return strategy.bothOperation(num1, num2);
	}
	
	public String getStrategyName() {
		return strategy.strategyName();
	}
}
