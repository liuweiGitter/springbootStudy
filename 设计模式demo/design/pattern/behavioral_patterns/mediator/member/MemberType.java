package design.pattern.behavioral_patterns.mediator.member;

import java.util.List;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 22:16:31
 * @desc 会员类型
 */
@Data
public class MemberType {
	// 会员类型id
	private int typeId;
	// 会员类型名称
	private String typeName;
	// 会员特权简介
	private String typeBrief;
	// 会员时间类型ids
	private List<String> timeIds;
	// 会员特权类型ids
	private List<String> privilegeIds;
}
