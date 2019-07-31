package design.pattern.behavioral_patterns.chain_of_responsibility.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liuwei
 * @date 2019-07-31 20:39:06
 * @desc 抽象责任类
 * 多个不同功能的对象都需要参与一个输入参数的处理，每个对象根据自己的需要执行自己的处理逻辑
 * 输入参数从一个对象开始，需要不断地被传递下去，传递过程中可能会对参数值进行修改或其它操作
 * 这种传参的链条，即是责任链
 * 由于需要传递给下一个责任人，当前责任人需要持有其指针，这种指针是不断循环下去的，因此最好持有自身
 * 每个责任人的处理逻辑是不一样的，因此持有的是自身的同类
 * 因此，所有的责任类必须继承自同一个抽象类，并实现同一抽象方法作为业务逻辑处理入口
 * 
 * Spring中无处不在的各种各样的注解，其处理器即是责任链类
 * 一个布满注解的类加载时，经过了一系列注解处理器的注解解析和处理后，才算是被Spring加载完成
 */
public abstract class AbstractRes {
	// 责任链上的下一个元素
	protected AbstractRes nextChainNode;

	// 责任链上的当前元素指定其下一个节点
	public void setNextChainNode(AbstractRes nextChainNode) {
		this.nextChainNode = nextChainNode;
	}

	// 责任处理标记：任务到来时传递同样类型的标记参数，检查标记判断是否需要当前责任人处理
	protected Set<String> doFlags;

	// 业务逻辑1入口：遍历整个链
	public void doLogic1(Set<String> flags, Object logicParam) {
		if (null == flags || flags.size() == 0) {
			return;
		}
		// 我自己是否需要处理
		if (isMyResponsibility(flags)) {
			System.out.print("逻辑链1：");
			realLogic(logicParam);
		}
		// 放行到下一责任人
		if (nextChainNode != null) {
			nextChainNode.doLogic1(flags, logicParam);
		}
	}

	// 业务逻辑2入口：遍历到被处理完
	public void doLogic2(Set<String> flags, Object logicParam) {
		if (null == flags || flags.size() == 0) {
			return;
		}
		// 我自己能否处理一部分
		if (canISolveSome(flags)) {
			System.out.print("逻辑链2：");
			realLogic(logicParam);
		}
		if (flags.size() > 0) {
			// 放行到下一责任人
			if (nextChainNode != null) {
				nextChainNode.doLogic2(flags, logicParam);
			}
		}
	}

	// 责任判断逻辑1：示例
	private boolean isMyResponsibility(Set<String> flags) {
		return null != doFlags && hasSetIntersect(flags);
	}

	// 责任判断逻辑2：示例
	private boolean canISolveSome(Set<String> flags) {
		return null == doFlags ? false : reduceSetIntersect(flags);
	}

	// 判断两个set集合是否有交集
	private boolean hasSetIntersect(Set<String> flags) {
		Iterator<String> iterator = flags.iterator();
		while (iterator.hasNext()) {
			if (doFlags.contains(iterator.next())) {
				return true;
			}
		}
		return false;
	}

	// 去掉两个set集合的交集
	private boolean reduceSetIntersect(Set<String> flags) {
		Set<String> intersect = null;
		Iterator<String> iterator = flags.iterator();
		while (iterator.hasNext()) {
			String flag = iterator.next();
			if (doFlags.contains(flag)) {
				if (null == intersect) {
					intersect = new HashSet<String>();
				}
				intersect.add(flag);
			}
		}
		if (null != intersect) {
			flags.removeAll(intersect);
		}
		return null != intersect;
	}

	// 实际业务处理逻辑
	abstract protected void realLogic(Object logicParam);
}
