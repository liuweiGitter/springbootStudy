package design.pattern.creational_patterns.prototype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.alibaba.fastjson.JSON;

/**
 * @author liuwei
 * @date 2019-07-28 11:48:03
 * @desc 深度拷贝对象工具类
 */
public class DeepCopyUtils {

	//使用序列化深度拷贝对象
	@SuppressWarnings("unchecked")
	public static <T> T serializableCopy(T obj){
		try {
			// 序列化
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);

			// 反序列化
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);

			return (T) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//使用fastjson深拷贝对象
	@SuppressWarnings("unchecked")
	public static <T> T jsonCopy(T obj) {
		String json = JSON.toJSONString(obj);
		return (T) JSON.parseObject(json, obj.getClass());
	}


}
