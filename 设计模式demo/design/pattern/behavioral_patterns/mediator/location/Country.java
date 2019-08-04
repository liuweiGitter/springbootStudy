package design.pattern.behavioral_patterns.mediator.location;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 21:32:29
 * @desc 国家
 */
@Data
public class Country {
	// 国家id
	private String countyId;
	// 所属大陆id
	private String continentId;
	// 国家名称
	private String countyName;
	// 国际编号
	private String countyCode;
	// 邮政编号
	private String mailCode;
	// 长途编号
	private String teleCode;
	// 时区id
	private String timeZoneId;
}
