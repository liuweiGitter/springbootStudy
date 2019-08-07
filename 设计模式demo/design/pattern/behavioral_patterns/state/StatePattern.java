package design.pattern.behavioral_patterns.state;

/**
 * @author liuwei
 * @date 2019-08-07 23:13:44
 * @desc 状态模式demo
 * 很多时候，一个对象的行为逻辑的实现取决于某个属性的值
 * 如果状态比较多或者逻辑比较复杂，使用if-else判断和实现逻辑分支，可能会使得对象方法膨胀
 * 希望在对象中持有状态对象，并在行为方法中直接调用状态对象的相应方法，而不必关注具体的实现逻辑
 * 具体的实现逻辑在每一个状态类的相应方法中定义
 * 如此，对象的行为方法将简化到最少量的代码，大量的代码被转移到状态类中
 * 
 * 需要将状态属性以及与之相关的行为方法抽取出一个状态类
 * 状态类中的行为方法的入参中持有状态所属的对象，以利用该对象实现具体的行为逻辑
 * 在行为逻辑完成后，需要改变对象的状态，通过状态的切换来实现行为逻辑的切换
 * 为完成状态的切换，使用多态，所有的状态类继承自同一状态接口
 * 
 * 需要注意的是，状态类的引入虽然简化了对象的方法实现，但却在系统中膨胀了类和对象的数量
 * 如果状态种类实在太多，可根据需要转移一部分重代码逻辑的状态实现到状态类中，保留另外的一部分在对象中
 */
public class StatePattern {
	
	public static void main(String[] args) {
		//创建笔记本对象，初始态为关机态
		Computer computer = new Computer();
		computer.setState(StateConstant.STOPING);
		//执行系列操作
		computer.openCover();
		computer.shortPress();
		computer.closeCover();
		
		computer.openCover();
		computer.shortPress();
		computer.longPress();
		computer.closeCover();
	}
	
}
