package design.pattern.behavioral_patterns.iterator;

/**
 * @author liuwei
 * @param <T>
 * @date 2019-08-01 21:14:27
 * @desc 迭代器接口
 * 为实现对集合对象的遍历，定义2个通用的遍历方法
 */
public interface Iterator<T> {
	boolean hasNext();
	T next();
}
