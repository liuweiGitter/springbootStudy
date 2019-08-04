package design.pattern.behavioral_patterns.mediator.user;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 13:12:34
 * @desc 用户--群关系
 */
@Data
public class UserGroup {
	// 用户id
	private int userId;
	// 群id
	private int groupId;
	// 用户昵称
	private String nickName;
	// 是否置顶群
	private boolean topFlag;
	// 是否消息免打扰
	private boolean msgNotWarnFlag;
}
