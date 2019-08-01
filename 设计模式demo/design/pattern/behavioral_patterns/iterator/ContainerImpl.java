package design.pattern.behavioral_patterns.iterator;

/**
 * @author liuwei
 * @date 2019-08-01 23:35:17
 * @desc 容器实现类
 */
public class ContainerImpl<T> implements Container<T> {

	// 内部聚合对象：本例定义为一个数组
	private transient T[] array;

	public ContainerImpl(T[] array) {
		this.array = array;
	}

	// 实现自己的私有迭代器
	@Override
	public Iterator<T> getIterator() {
		return new InnerIterator();
	}

	private class InnerIterator implements Iterator<T> {

		int index;

		@Override
		public boolean hasNext() {
			if (index < array.length) {
				return true;
			}
			return false;
		}

		@Override
		public T next() {
			if (this.hasNext()) {
				return array[index++];
			}
			return null;
		}

	}

}
