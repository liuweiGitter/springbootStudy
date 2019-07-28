package design.pattern.creational_patterns.prototype;

import design.pattern.creational_patterns.prototype.clone.EntityDeepClone;
import design.pattern.creational_patterns.prototype.clone.EntityLightClone;
import design.pattern.creational_patterns.prototype.clone.Helper;
import design.pattern.creational_patterns.prototype.fastjson.Entity;
import design.pattern.creational_patterns.prototype.serializable.EntitySerializable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-27 23:14:05
 * @desc 原型模式demo
 * 原型模式是一种复制对象的模式，复制对象是建立一个全新内存对象的方式，而不仅仅是指针引用
 * new对象可以创建一个全新的对象，并可以使其所有字段值的内容和源对象值完全相同
 * 不过，如果存在深层次的引用依赖，需要一层层new对象，代码量将不可控
 * 另外，有些时候，new对象很消耗资源，以致于应该选择另外的相比更高效的复制对象方式
 * 大多数情况下，new复制都是最高效的(只是赋值代码可能非常多而已)
 * 但由于代码量实在膨胀，即使高效的情况下，通常也不会使用new复制
 * 原型模式是除了new对象以外使用其它方式复制对象的一种设计模式的统称
 * 复制对象有很多种办法，没有绝对的优劣之分，根据实际需要选择恰当的方式
 * 所有的对象复制方法，都只会进行对象级属性的复制，对于类变量则不会复制
 * jdk自身提供了3种常见的对象复制方式：克隆、序列化和反射
 * 1.克隆：继承自Object类的clone方法，该方法为protected方法，需要重写为public并调用父类的clone方法
 * 克隆分为浅克隆和深克隆，浅克隆几乎没有意义，深克隆又比较困难，实际应用中，基本不会使用克隆复制对象
 * 2.序列化：实现自序列化接口，该接口为标记接口，不需实现任何方法
在类中定义一个public的拷贝方法，方法中实现对象的序列化写和反序列化读并返回读到的对象即可
序列化的优点是使用方便，代码简单，缺点是，依赖的所有引用类型都需要实现序列化，而且绝大多数情况下，速度相对new赋值慢很多
 * 实际应用中，经常使用此类方式复制对象，但由于效率低，并不推荐这样做
 * 3.反射：反射遍历所有对象级属性，并重新为新对象赋值，这种方式自己需要实现的代码逻辑最复杂，但效率比序列化高一些
 * 实际应用中，较少使用，但相比序列化，更推荐如此
 * 由于逻辑复杂且应用较少，本demo不做演示
 * 
 * 除了利用jdk自身的特性外，还可以使用阿里巴巴的fastjson进行对象的深度拷贝
 * 这种方式效率比序列化更高，自己需要实现的代码逻辑最简单，最佳推荐
 */
@Slf4j
public class PrototypePattern {
	
	public static void main(String[] args) {
		//浅克隆
		lightClone(false);
		//深克隆
		deepClone(false);
		//序列化深拷贝
		serializableCopy(false);
		//fastjson深拷贝
		jsonCopy(false);
		
	}
	
	private static void lightClone(boolean start) {
		if (!start) {
			return;
		}
		//new对象
		EntityLightClone light = new EntityLightClone();
		light.objectValue.setName("Helper18");
		light.objectValue.setAge(18);;
		light.printMsg("\n>>>[light origin]");
		//浅克隆对象
		EntityLightClone lightClone = (EntityLightClone) light.clone();
		lightClone.printMsg("\n>>>[light copy]");
		
		//克隆后，两个对象指向不同的内存地址
		log.info("\n>>>light和lightClone是否指向同一引用地址："
				+(light==lightClone));
		
		//克隆后，两个对象的引用字段值指向了同一内存地址(浅克隆)
		log.info("\n>>>stringValue是否指向同一引用地址："
		+(light.stringValue==lightClone.stringValue));
		log.info("\n>>>objectValue是否指向同一引用地址："
				+(light.objectValue==lightClone.objectValue));
		
		/**
		 * 修改其中任意一个对象的引用字段指向新地址后，两个对象的该引用字段指向了2个不同的内存地址
		 * 但尤其需要注意的是，对于直接常量赋值的字符串以及包装类等常量值，内存中只会存在一份
		 * null值内存中也只存在一份，所有的null值地址都相等
		 */
		log.info("\n\n------修改后------\n");
		
		light.stringValue=new String("5");//新建内存地址，false
		log.info("\n>>>stringValue是否指向同一引用地址："+(light.stringValue==lightClone.stringValue));
		light.stringValue="5";//使用原内存地址，true
		log.info("\n>>>stringValue是否指向同一引用地址："+(light.stringValue==lightClone.stringValue));
		
		lightClone.objectValue.setName("Helper19");light.objectValue.setAge(19);//使用原内存地址，true
		log.info("\n>>>objectValue是否指向同一引用地址："+(light.objectValue==lightClone.objectValue));
		lightClone.objectValue=new Helper();//新建内存地址，false
		log.info("\n>>>objectValue是否指向同一引用地址："+(light.objectValue==lightClone.objectValue));
	}
	
	private static void deepClone(boolean start) {
		if (!start) {
			return;
		}
		//new对象
		EntityDeepClone deep = new EntityDeepClone();
		deep.objectValue.setName("Helper18");
		deep.objectValue.setAge(18);;
		deep.printMsg("\n>>>[deep origin]");
		//深克隆对象
		EntityDeepClone deepClone = (EntityDeepClone) deep.clone();
		deepClone.printMsg("\n>>>[deep copy]");
		
		//克隆后，两个对象指向不同的内存地址
		log.info("\n>>>deep和deepClone是否指向同一引用地址："
				+(deep==deepClone));
		
		//克隆后，两个对象的引用字段值指向了不同的内存地址(深克隆)
		log.info("\n>>>stringValue是否指向同一引用地址："
		+(deep.stringValue==deepClone.stringValue));
		log.info("\n>>>objectValue是否指向同一引用地址："
				+(deep.objectValue==deepClone.objectValue));
		
		//克隆后，两个对象的引用字段的引用字段值指向了不同的内存地址(深克隆)
		log.info("\n>>>objectValue的name字段是否指向同一引用地址："
		+(deep.objectValue.getName()==deepClone.objectValue.getName()));

	}

	private static void serializableCopy(boolean start) {
		if (!start) {
			return;
		}
		//new对象
		EntitySerializable deep = new EntitySerializable();
		deep.objectValue.setName("Helper18");
		deep.objectValue.setAge(18);;
		deep.printMsg("\n>>>[deep origin]");
		//深拷贝对象
		EntitySerializable deepCopy = DeepCopyUtils.serializableCopy(deep);
		deepCopy.printMsg("\n>>>[deep copy]");
		
		//拷贝后，两个对象指向不同的内存地址
		log.info("\n>>>deep和deepCopy是否指向同一引用地址："
				+(deep==deepCopy));
		
		//拷贝后，两个对象的引用字段值指向了不同的内存地址
		log.info("\n>>>stringValue是否指向同一引用地址："
		+(deep.stringValue==deepCopy.stringValue));
		log.info("\n>>>objectValue是否指向同一引用地址："
				+(deep.objectValue==deepCopy.objectValue));
		
		//拷贝后，两个对象的引用字段的引用字段值指向了不同的内存地址
		//注意，由于此处deepCopy的objectValue未被序列化，因此反序列化后为null，因此此处报错空指针
		log.info("\n>>>objectValue的name字段是否指向同一引用地址："
		+(deep.objectValue.getName()==deepCopy.objectValue.getName()));

	}

	private static void jsonCopy(boolean start) {
		if (!start) {
			return;
		}
		//new对象
		Entity deep = new Entity();
		deep.objectValue.setName("Helper18");
		deep.objectValue.setAge(18);;
		deep.printMsg("\n>>>[deep origin]");
		//深拷贝对象
		Entity deepCopy = DeepCopyUtils.jsonCopy(deep);
		deepCopy.printMsg("\n>>>[deep copy]");
		
		//拷贝后，两个对象指向不同的内存地址
		log.info("\n>>>deep和deepCopy是否指向同一引用地址："
				+(deep==deepCopy));
		
		//拷贝后，两个对象的引用字段值指向了不同的内存地址
		log.info("\n>>>stringValue是否指向同一引用地址："
		+(deep.stringValue==deepCopy.stringValue));
		log.info("\n>>>objectValue是否指向同一引用地址："
				+(deep.objectValue==deepCopy.objectValue));
		
		//拷贝后，两个对象的引用字段的引用字段值指向了不同的内存地址
		log.info("\n>>>objectValue的name字段是否指向同一引用地址："
		+(deep.objectValue.getName()==deepCopy.objectValue.getName()));
		
	}
}
