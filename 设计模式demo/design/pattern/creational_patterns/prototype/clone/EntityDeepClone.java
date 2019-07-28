package design.pattern.creational_patterns.prototype.clone;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-28 01:02:30
 * @desc 深克隆的类(原型类)
 * 由于对象A克隆时，其引用类型属性B是拷贝引用地址而不是新建，因此，直接克隆对象属于浅克隆
 * 只有在克隆A时将其引用类型属性B也克隆一份再赋值
 * 且其引用类型属性B在其clone方法中也对其引用类型属性C克隆，依次类推到所有引用链，才是彻底的深度克隆
 * 现实中的很多对象依赖链很长，要做到彻底的深度克隆是非常困难且几乎不切实际的
 * 本文仅做深度克隆演示，实际操作中，基本不会使用克隆来实现深度拷贝，有另外的办法实现深度拷贝
 */
@Slf4j
public class EntityDeepClone implements Cloneable {
	public int basicValue = 5;
	public String stringValue = "5";
	public Helper objectValue = new Helper();
	
	public String getDesc() {
		return "深克隆的类(原型类)";
	}
	
	@Override
	public Object clone() {
		try {
			//0.基本值不需克隆
			//1.字符串的克隆
			String cloneStr = new String(stringValue);
			//2.引用对象的克隆：内部也深度克隆
			Helper cloneObj = (Helper) objectValue.clone();
			//创建深克隆对象
			EntityDeepClone clone = (EntityDeepClone) super.clone();
			clone.stringValue = cloneStr;
			clone.objectValue = cloneObj;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void printMsg(String preffix) {
		if (null==preffix) {
			preffix="";
		}
		log.info(preffix+"hashcode:"+hashCode()+",basicValue:"+basicValue
				+",stringValue:"+stringValue+",objectValue:"+objectValue);
	}
}
