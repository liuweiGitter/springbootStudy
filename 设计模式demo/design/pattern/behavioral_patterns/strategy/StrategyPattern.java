package design.pattern.behavioral_patterns.strategy;

import design.pattern.behavioral_patterns.strategy.strategy.OperationAdd;
import design.pattern.behavioral_patterns.strategy.strategy.OperationDiv;
import design.pattern.behavioral_patterns.strategy.strategy.OperationMulti;
import design.pattern.behavioral_patterns.strategy.strategy.OperationPower;
import design.pattern.behavioral_patterns.strategy.strategy.OperationSub;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-08-08 21:04:33
 * @desc 策略模式demo
 */
@Slf4j
public class StrategyPattern {
	
	public static void main(String[] args) {
		//创建计算对象
		Calculate calculate = new Calculate();
		int num1 = 8;
		int num2 = 4;
		//选择计算策略并计算
		calculate.setStrategy(new OperationAdd());
		msg(calculate,num1,num2);
		
		calculate.setStrategy(new OperationSub());
		msg(calculate,num1,num2);
		
		calculate.setStrategy(new OperationMulti());
		msg(calculate,num1,num2);
		
		calculate.setStrategy(new OperationDiv());
		msg(calculate,num1,num2);
		
		calculate.setStrategy(new OperationPower());
		msg(calculate,num1,num2);
	}
	
	private static void msg(Calculate calculate,int num1,int num2) {
		log.info("\n当前策略:"+calculate.getStrategyName()+
				",计算结果:"+calculate.executeStrategy(num1, num2));
	}
	
}
