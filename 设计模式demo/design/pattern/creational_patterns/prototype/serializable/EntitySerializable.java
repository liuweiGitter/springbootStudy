package design.pattern.creational_patterns.prototype.serializable;

import java.io.Serializable;

import design.pattern.creational_patterns.prototype.clone.Helper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-28 10:51:28
 * @desc 使用序列化和反序列化实现对象的深度拷贝
 * 序列化：将java对象写入二进制字节流中
 * 反序列化：从二进制字节流中读取出java对象
 * 原对象存在JVM中，从虚拟机中读取对象到一个字节流中，再将流反序列化出一个java对象，存在新的内存地址中，如此就实现了对原对象的深度拷贝
 * 序列化和反序列化即是深度拷贝的一个中介过程
 * 需要注意的是，对象的所有引用字段xxx也需要序列化，否则会报错xxx未实现序列化
 * 如果的确需要对某些字段屏蔽序列化(如可能需要网络传输或文件存储的敏感字段)，需要对该字段添加transient
 */


@Slf4j
public class EntitySerializable implements Serializable{
	private static final long serialVersionUID = 1L;
	public String stringValue = "5";
	public int basicValue = 5;
	//对objectValue屏蔽序列化：不进行序列化写入，在反序列化时还原为null
	public transient Helper objectValue = new Helper();
	
	public void printMsg(String preffix) {
		if (null==preffix) {
			preffix="";
		}
		log.info(preffix+"hashcode:"+hashCode()+",basicValue:"+basicValue
				+",stringValue:"+stringValue+",objectValue:"+objectValue);
	}
}
