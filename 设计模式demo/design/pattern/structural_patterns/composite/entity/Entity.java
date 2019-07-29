package design.pattern.structural_patterns.composite.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-07-29 21:05:31
 * @desc 组合类：类中持有自身的列表，相同对象的树形菜单式的组织，可自由扩展下一层组织
 */
@Data
public class Entity {
	private String nodeName;
	private String nodeId;
	private String parentId;
	List<Entity> children;

	public Entity(String nodeName, String nodeId, String parentId) {
		super();
		this.nodeName = nodeName;
		this.nodeId = nodeId;
		this.parentId = parentId;
		this.children = new ArrayList<Entity>();
	}

	public boolean addChild(Entity e) {
		return children.add(e);
	}

	public boolean removeChild(String childNodeId) {
		for (Entity entity : children) {
			if (entity.nodeId.equals(childNodeId)) {
				children.remove(entity);
				return true;
			}
		}
		return false;
	}
	
	public boolean removeChild(Entity e) {
		return children.remove(e);
	}
	
	public void clearChildren() {
		children = new ArrayList<Entity>();
	}
	
	public Entity getChild(String childNodeId) {
		for (Entity entity : children) {
			if (entity.nodeId.equals(childNodeId)) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Entity [nodeName=" + nodeName + ", nodeId=" + nodeId + ", parentId=" + parentId + ", childrenSize="
				+ children.size() + "]";
	}

}
