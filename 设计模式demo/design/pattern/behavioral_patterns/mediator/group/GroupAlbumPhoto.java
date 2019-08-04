package design.pattern.behavioral_patterns.mediator.group;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 12:58:43
 * @desc 群相册相片
 */
@Data
public class GroupAlbumPhoto {
	// 相册id
	private String albumId;
	// 相片url路径
	private String urlPath;
	// 上传时间
	private LocalDateTime date;
	// 上传用户id
	private String userId;
	// 上传说明
	private String comment;
}
