package design.pattern.behavioral_patterns.mediator.member;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 22:26:18
 * @desc 会员时间类型
 */
@Data
public class MemberTimeType {
	// 会员类型id
	private int memberTypeId;
	// 会员时间类型id
	private String typeId;
	// 会员时间类型名称
	private String typeName;
	// 会员价格原价
	private float priceBefore;
	// 会员价格现价
	private float priceNow;
}
