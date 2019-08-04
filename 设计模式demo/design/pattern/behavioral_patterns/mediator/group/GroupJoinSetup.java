package design.pattern.behavioral_patterns.mediator.group;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-04 00:25:10
 * @desc 加群设置
 */
@Data
public class GroupJoinSetup {
	// 加群设置id
	private String setupId;
	// 加群是否自动审批
	private boolean autoApprovalFlag;
	// 加群方式
	private String joinStyle;
	// 群是否允许被搜索
	private boolean searchAllowedFlag;
	// 群被搜索方式
	private String searchStyle;
}
