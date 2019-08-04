package design.pattern.behavioral_patterns.mediator.group;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 12:10:52
 * @desc 群文件
 */
@Data
public class GroupFile {
	// 群id
	private int groupId;
	// 文件名
	private String fileName;
	// 文件url路径
	private String urlPath;
	// 是否目录
	private boolean dirFlag;
	// 包含文件数量
	private short childNodeCount;
	// 文件作者
	private String authorId;
	// 发布时间
	private LocalDateTime createTime;
	// 文件大小(字节)
	private int byteSize;
	// 下载人数
	private int downLoadCount;
}
