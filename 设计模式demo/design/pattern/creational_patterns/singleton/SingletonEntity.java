package design.pattern.creational_patterns.singleton;

/**
 * @author liuwei
 * @date 2019-07-27 14:37:03
 * @desc 单例实体类
 * 构造方法必须私有--不让外部new对象 静态方法返回实例--但又要让外界能够获取对象
 * 内部持有私有的静态实例--全局单例需要长期保存在内存中，以免反复创建单例
 * 外部获取单例的时机(效率)和线程安全性可根据实际需要定义
 * 实际上，除非的确需要延迟加载，大多数情况下，启动加载方式是最佳实践
 */
public class SingletonEntity {
	private static SingletonEntity instance;

	private SingletonEntity() {}
	
	/**
	 * Any method or property this class owner
	 * 除了单例方法以外的一个普通类中可以具备的其它所需的属性和方法
	 */
	public String getDesc() {
		return "单例实体类SingletonEntity";
	}
	

	//1.启动加载
	//启动加载都是线程安全的，静态私有实例直接创建对象赋值
	static {
		instance = new SingletonEntity();
	}
	public static SingletonEntity getInstanceUnLazy() {  
	    return instance;
	}
	
	//2.延迟加载+线程不安全
	public static SingletonEntity getInstanceLazyAndUnsafe() {
		if (instance == null) {  
	        instance = new SingletonEntity();  
	    }
	    return instance;
	}
	
	//3.延迟加载+线程安全
	//方法级同步，低效
	public static synchronized SingletonEntity getInstanceLazyAndSafe1() {
		if (instance == null) {  
	        instance = new SingletonEntity();  
	    }  
	    return instance;
	}
	
	//4.延迟加载+线程安全
	//代码级同步，高效
	public static SingletonEntity getInstanceLazyAndSafe() {
		if (instance == null) {  
	        synchronized (SingletonEntity.class) {  
		        if (instance == null) {  
		        	instance = new SingletonEntity();  
		        }
	        }
	    }  
	    return instance;
	}
	
}
