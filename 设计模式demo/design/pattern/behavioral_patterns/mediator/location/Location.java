package design.pattern.behavioral_patterns.mediator.location;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 22:00:18
 * @desc 地址
 */
@Data
public class Location {
	// 地址id
	private String locationId;
	// 国家id
	private String countryId;
	// 省id
	private String provinceId;
	// 城市id
	private String cityId;
	// 县/区id
	private String townId;
}
