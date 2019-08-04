package design.pattern.behavioral_patterns.mediator.zone;

import java.util.List;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-04 00:51:34
 * @desc 用户空间
 */
@Data
public class UserZone {
	// 用户id
	private int userId;
	// 留言列表ids
	private List<String> msgIds;
	// 访客列表ids
	private List<String> visitorMsgIds;
	// 日志列表ids
	private List<String> logIds;
	// 说说列表ids
	private List<String> talkIds;
	// 相册列表ids
	private List<String> albumIds;
}
