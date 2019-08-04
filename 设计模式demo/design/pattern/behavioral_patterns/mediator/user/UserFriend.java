package design.pattern.behavioral_patterns.mediator.user;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 23:31:55
 * @desc 用户--好友关系
 */
@Data
public class UserFriend {
	// 用户id
	private int userId;
	// 好友id
	private int friendId;
	// 好友备注名称
	private int friendName;
	// 好友分组id
	private String teamId;
	// 是否特别关心
	private boolean carrierFlag;
	// 是否屏蔽此人
	private boolean blockFlag;
	// 此人消息是否免打扰
	private boolean msgNotWarnFlag;
	// 是否隐藏到不常用联系人
	private boolean lessConnectFlag;
	// 是否屏加入到黑名单
	private boolean blackListFlag;
}
