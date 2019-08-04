package design.pattern.behavioral_patterns.mediator.team;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 23:49:11
 * @desc 好友分组
 */
@Data
public class FriendTeam {
	// 用户id
	private int userId;
	// 分组id
	private String teamId;
	// 分组名称
	private String teamName;
}
