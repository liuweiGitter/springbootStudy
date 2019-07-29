package design.pattern.structural_patterns.composite;

import java.util.ArrayList;
import java.util.List;

import design.pattern.structural_patterns.composite.entity.Entity;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-29 00:52:11
 * @desc 组合模式demo
 */
@Slf4j
public class CompositePattern {
	
	public static void main(String[] args) {
		Entity root = new Entity("0001节点", "0001", "-1");
		Entity second1 = new Entity("0011节点", "0011", "0001");
		Entity second2 = new Entity("0021节点", "0021", "0001");
		Entity third11 = new Entity("0111节点", "0111", "0011");
		Entity third12 = new Entity("0211节点", "0211", "0011");
		Entity third21 = new Entity("0121节点", "0121", "0021");
		Entity third22 = new Entity("0221节点", "0221", "0021");
		//构建组织
		second1.addChild(third11);
		second1.addChild(third12);
		second2.addChild(third21);
		second2.addChild(third22);
		
		root.addChild(second1);
		root.addChild(second2);
		//纵向递归遍历打印组织树
		climbTreeVertical(root,false);
		//横向递归遍历打印组织树
		climbTreeHorizon(root,true);
		
	}
	
	private static void climbTreeVertical(Entity entity,boolean start) {
		if (!start) {
			return;
		}
		log.info(entity.toString());
		for (Entity child : entity.getChildren()) {
			climbTreeVertical(child,true);
		}
	}

	private static void climbTreeHorizon(Entity entity,boolean start) {
		if (!start) {
			return;
		}
		log.info(entity.toString());
		climbTreeHorizon(entity.getChildren());
	}
	
	private static void climbTreeHorizon(List<Entity> entitys) {
		List<Entity> children = new ArrayList<Entity>();
		for (Entity entity : entitys) {
			log.info(entity.toString());
			children.addAll(entity.getChildren());
		}
		if (children.size()>0) {		
			climbTreeHorizon(children);
		}
	}
}
