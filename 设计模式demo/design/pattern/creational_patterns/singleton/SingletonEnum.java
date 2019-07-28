package design.pattern.creational_patterns.singleton;

/**
 * @author liuwei
 * @date 2019-07-27 15:15:34
 * @desc 枚举方式的单例模式
 */
public enum SingletonEnum {
	INSTANCE;
	/**
	 * Any method or property this class owner
	 * 一个普通枚举类中可以具备的其它所需的属性和方法
	 */
	public String getDesc() {
		return "单例枚举类SingletonEnum";
	}
}
