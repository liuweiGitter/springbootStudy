package design.pattern.behavioral_patterns.strategy.strategy;

/**
 * @author liuwei
 * @date 2019-08-08 21:09:21
 * @desc 策略接口
 * 所谓策略，实际为对象行为逻辑(即方法函数)的算法
 * 一种策略对应一种代码逻辑，用以实现一种功能
 * 策略接口中定义了不同策略实现都需要执行的一组方法
 * 策略内部持有这些方法，以运行其实现的代码逻辑
 * 对象内部持有策略对象，以利用策略实现具体的行为逻辑
 * 
 * 策略模式相似于状态模式，都可以消除方法逻辑的if-else分支
 * 不同的是，状态本身就是对象的属性，策略则是独立于对象存在的，对象依赖策略，而不是包含策略
 * 同样地，如果策略实在太多，应使用混合模式，以避免策略类膨胀
 * 
 * 本例为算数运算的策略接口示例
 */
public interface Strategy {
	//两个数操作
	int bothOperation(int num1, int num2);
	//策略名称
	String strategyName();
}
