package design.pattern.behavioral_patterns.mediator.group;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 12:50:01
 * @desc 群相册
 */
@Data
public class GroupAlbum {
	// 群id
	private int groupId;
	// 相册id
	private String albumId;
	// 相册名
	private String albumName;
	// 相册url路径
	private String urlPath;
	// 相片数量
	private short photoCount;
}
