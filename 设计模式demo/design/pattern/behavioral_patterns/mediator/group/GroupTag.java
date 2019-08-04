package design.pattern.behavioral_patterns.mediator.group;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 11:04:37
 * @desc 群标签
 */
@Data
public class GroupTag {
	//标签id
	private String tagId;
	//标签名
	private String tagName;
	//相关群数量
	private int relativeGroupCount;
}
