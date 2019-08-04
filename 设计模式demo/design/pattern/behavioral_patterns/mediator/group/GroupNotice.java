package design.pattern.behavioral_patterns.mediator.group;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 11:52:25
 * @desc 群公告
 */
@Data
public class GroupNotice {
	// 群id
	private int groupId;
	// 公告id
	private String noticeId;
	// 公告文本
	private String contentText;
	// 公告图片url路径
	private String urlPath;
	// 公告作者
	private String authorId;
	// 发布时间
	private LocalDateTime createTime;
	// 是否置顶
	private boolean topFlag;
	// 是否发给新成员
	private boolean toFreshManFlag;
	// 已读人数
	private short readedCount;
}
