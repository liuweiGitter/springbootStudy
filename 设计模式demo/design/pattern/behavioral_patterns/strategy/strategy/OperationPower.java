package design.pattern.behavioral_patterns.strategy.strategy;

/**
 * @author liuwei
 * @date 2019-08-08 21:32:57
 * @desc 乘方操作(算法即策略)
 */
public class OperationPower implements Strategy {

	@Override
	public int bothOperation(int num1, int num2) {
		if (num2==0) {
			return 1;
		}else if (num2>0) {
			int temp = num1;
			for (int i = 1; i < num2; i++) {
				num1*=temp;
			}
			return num1;
		}else {
			int temp = num1;
			num2*=-1;
			for (int i = 1; i < num2; i++) {
				num1*=temp;
			}
			return 1/num1;
		}
	}

	@Override
	public String strategyName() {
		return "乘方策略";
	}

}
