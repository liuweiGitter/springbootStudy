package design.pattern.behavioral_patterns.mediator.user;

import java.time.LocalDateTime;
import java.util.List;

import design.pattern.behavioral_patterns.mediator.MsgPlateform;
import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 10:28:58
 * @desc 用户
 */
@Data
public class User {
	// 用户id
	private int userId;
	// 用户名
	private String userName;
	// 性别
	private Boolean gender;
	// 出生日期
	private LocalDateTime birthDay;
	// 个性签名
	private String signText;
	// 头像url路径
	private String headPhotoUrl;
	// 个人简介
	private String selfBrief;
	// 职业
	private String career;
	// 公司
	private String companyName;
	// 所在地id
	private String regionId;
	// 家乡id
	private String homeTownId;
	// 邮箱
	private String email;
	// 手机号
	private String phoneNum;
	// 用户等级
	private String level;
	// 已开通会员类型
	private List<String> memberTypeIds;
	// 在线状态id
	private Byte statusId;
	
	public User() {
		
	}

	public User(int userId) {
		super();
		this.userId = userId;
	}
	
	public User(int userId, String userName) {
		super();
		this.userId = userId;
		this.userName = userName;
	}
	
	public void sendMsg(String msg,Object obj) {
		MsgPlateform.sendMsg(this,msg,obj);
	}

}
