package design.pattern.behavioral_patterns.mediator.location;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 21:54:58
 * @desc 城市
 */
@Data
public class City {
	// 省id
	private String provinceId;
	// 城市id
	private String cityId;
	// 城市名称
	private String cityName;
}
