package design.pattern.behavioral_patterns.mediator.group;

import java.util.List;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 10:38:19
 * @desc 群
 */
@Data
public class Group {
	//群id
	private int groupId;
	//群名
	private String groupName;
	//群简介
	private String groupBrief;
	//群标签ids
	private List<String> tagIds;
	//群等级
	private String groupLevel;
	//群logo路径
	private String groupLogoPath;
	//群封面路径
	private String groupFacePath;
	//群地点名称
	private String groupAddress;
	//群容量类型id
	private String groupContainerTypeId;
	//群主题类型id
	private String groupThemeTypeId;
	//群人数
	private short totalCount;
	//群创建人id
	private int createrId;
	//群管理员ids
	private List<Integer> managerIds;
	//5个省市分布的ids
	private List<Integer> regionIds;
	//群是否禁言
	private boolean forbiddenTalk;
	//群成员权限id
	private String memberPrivilegeId;
	//加群设置id
	private String joinGroupSetupId;
	//群信用等级
	private Byte creditLevel;
	//群头衔设置id
	private String titleSetupId;
	
	public Group() {
	}
	
	public Group(int groupId) {
		this.groupId = groupId;
	}
	
}
