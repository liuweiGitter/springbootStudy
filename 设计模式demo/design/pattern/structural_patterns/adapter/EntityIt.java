package design.pattern.structural_patterns.adapter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-28 19:59:14
 * @desc 需要被调用功能的类
 * 该类中有一些方法需要被调用，但这些方法中提供的功能已经不能完全满足新的需要(比如新增逻辑判断分支等)
 * 而这个类因为某种原因不能或者不方便被更改(如三方类、旧版本类等)，以致于需要在新类中增强功能
 * 新类中构建同名的或任意命名的方法(最好同名)，并在其中增加功能扩展逻辑即可
 */
@Slf4j
public class EntityIt {

	//需要被扩展功能的方法
	public void methodA() {
		log.info("调用EntityIt的方法methodA");
	}
	
	//需要被扩展功能的方法
	public void methodB() {
		log.info("调用EntityIt的方法methodB");
	}
	
	//不需要被扩展功能的方法
	public void methodFree() {
		log.info("我是一个不需要被扩展功能的方法");
	}
}
