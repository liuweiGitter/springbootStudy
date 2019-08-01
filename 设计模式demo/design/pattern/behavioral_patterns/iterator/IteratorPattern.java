package design.pattern.behavioral_patterns.iterator;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-08-01 21:10:08
 * @desc 迭代器模式demo
 */
@Slf4j
public class IteratorPattern {
	
	public static void main(String[] args) {
		String[] array = {"liu","wei","iterator","pattern"};
		ContainerImpl<String> container = new ContainerImpl<String>(array);
		Iterator<String> iterator = container.getIterator();
		while (iterator.hasNext()) {
			log.info(iterator.next());
		}
	}
	
}
