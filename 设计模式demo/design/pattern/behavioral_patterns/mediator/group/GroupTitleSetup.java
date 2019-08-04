package design.pattern.behavioral_patterns.mediator.group;

import java.util.Map;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-04 00:35:46
 * @desc 群头衔设置
 */
@Data
public class GroupTitleSetup {
	// 群id
	private int groupId;
	// 群头衔设置id
	private String titleSetupId;
	// 聊天窗口显示成员头衔
	private boolean titleShowFlag;
	// Lv1活跃头衔
	private String titleLv1;
	// Lv2活跃头衔
	private String titleLv2;
	// Lv3活跃头衔
	private String titleLv3;
	// Lv4活跃头衔
	private String titleLv4;
	// Lv5活跃头衔
	private String titleLv5;
	// Lv6活跃头衔
	private String titleLv6;
	// 专属头衔
	private Map<Integer, String> userTitleMap;
}
