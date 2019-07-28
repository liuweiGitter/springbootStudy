package design.pattern.creational_patterns.singleton;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-27 15:19:58
 * @desc 单例模式demo
 */
@Slf4j
public class SingletonPattern {
	
	public static void main(String[] args) {
		//单例类对象获取
		SingletonEntity singletonEntity = SingletonEntity.getInstanceUnLazy();
		singletonEntity = SingletonEntity.getInstanceLazyAndUnsafe();
		singletonEntity = SingletonEntity.getInstanceLazyAndSafe1();
		singletonEntity = SingletonEntity.getInstanceLazyAndSafe();
		log.info(singletonEntity.getDesc());
		//单例枚举类对象获取
		SingletonEnum singletonEnum = SingletonEnum.INSTANCE;
		log.info(singletonEnum.getDesc());
	}
}
