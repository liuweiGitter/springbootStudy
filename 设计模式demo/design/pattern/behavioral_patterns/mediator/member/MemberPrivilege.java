package design.pattern.behavioral_patterns.mediator.member;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 22:47:20
 * @desc 会员特权
 */
@Data
public class MemberPrivilege {
	// 会员特权id
	private int privilegeId;
	// 特权名称
	private String privilegeName;
	// 特权类型id
	private String typeId;
	// 特权简介
	private String brief;
	// 特权介绍文档id
	private String contentId;
	// 特权图标id
	private String logoId;
}
