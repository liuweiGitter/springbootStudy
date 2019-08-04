package design.pattern.behavioral_patterns.mediator.group;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 10:56:38
 * @desc 群成员统计
 */
@Data
public class GroupStatistics {
	//群id
	private int groupId;
	//群人数
	private short totalCount;
	//top8活跃群成员ids
	private List<Integer> activeUserIds;
	//性别统计
	private short maleCount;
	private short femaleCount;
	//单身人数统计
	private short singleCount;
	//省市分布
	private Map<String, Integer> regionCountMap;
	//年龄分布
	private Map<String, Integer> agePercentMap;
	
}
