package design.pattern.behavioral_patterns.mediator.group;

import java.util.List;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 23:10:28
 * @desc 群容量类型
 */
@Data
public class GroupContainerType {
	// 群容量类型id
	private int groupTypeId;
	// 群人数容量
	private short memberMax;
	// 群存储空间容量
	private int storeMax;
	// 存储空间单位MB或GB或TB
	private short storeUnit;
	// 群管理员容量
	private Byte managerMax;
	// 是否为付费群
	private boolean payFlag;
	// 付费类型ids
	private List<String> groupPayTypeIds;
}
