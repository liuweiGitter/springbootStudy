package design.pattern.behavioral_patterns.mediator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import design.pattern.behavioral_patterns.mediator.group.Group;
import design.pattern.behavioral_patterns.mediator.user.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-08-04 23:00:07
 * @desc 消息通讯平台
 * 该类用以处理用户对象之间的通信，接收一方消息，推送到另一方，属于通信中介
 * 行为如接发消息，虽然是用户的，但通信逻辑实现比较复杂，最好和用户对象解耦而单独在一个类中实现
 * 中介和代理的不同在于，后者主要用以控制行为，而前者则主要用以处理通信
 */
@Slf4j
public class MsgPlateform {

	private final static String FORMATE_STYLE = "yyyy-MM-dd HH:mm:ss";
	private final static DateTimeFormatter FORMATEER = DateTimeFormatter.ofPattern(FORMATE_STYLE);

	private static String dateFormate(LocalDateTime time) {
		return time.format(FORMATEER);
	}

	/**
	 * 模拟复杂的通信过程
	 * @param user 发送者用户
	 * @param msg  消息内容
	 * @param obj  目标用户或群对象
	 */
	public static void sendMsg(User user, String msg, Object obj) {
		boolean isGroup = false;
		int targetId = 0;
		if (obj instanceof Group) {
			isGroup = true;
			targetId = ((Group) obj).getGroupId();
		} else if (obj instanceof User) {
			targetId = ((User) obj).getUserId();
		} else {
			log.info("目标类型错误！");
		}
		log.info("判断网络是否可用");
		log.info("确认目标群是否禁言");
		log.info("确认目标用户或群是否屏蔽消息");
		log.info("拼接消息");
		System.out.println("[time]" + dateFormate(LocalDateTime.now()) + "\n[fromUserId]" + user.getUserId()
				+ (isGroup ? "\n[toGroupId]" : "\n[toUserId]") + targetId);
		System.out.println("[msg]" + msg);
		log.info("建立网络连接");
		log.info("发送到消息队列");
		log.info("通信异常处理");
		log.info("建立网络连接");
		log.info("路由目标寻址");
		log.info("推送到目标用户或群");
		log.info("确认目标用户或群是否收到");
		log.info("通信异常处理");
		log.info("通信日志记录");
	}

}
