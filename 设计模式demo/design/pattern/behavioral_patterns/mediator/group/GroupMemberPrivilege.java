package design.pattern.behavioral_patterns.mediator.group;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-04 00:16:55
 * @desc 群成员权限
 */
@Data
public class GroupMemberPrivilege {
	// 群成员权限id
	private String privilegeId;
	// 是否允许群成员邀请好友加群
	private boolean memberInviteFlag;
	// 群成员邀请方式
	private String memberInviteStyle;
	// 是否允许普通群成员发起临时会话
	private boolean memberTempSessionFlag;
	// 是否允许普通群成员发起新的群聊
	private boolean memberNewTalkFlag;
	// 是否允许普通群成员上传文件
	private boolean memberUploadFileFlag;
	// 是否允许普通群成员上传相册
	private boolean memberUploadAlbumFlag;
}
