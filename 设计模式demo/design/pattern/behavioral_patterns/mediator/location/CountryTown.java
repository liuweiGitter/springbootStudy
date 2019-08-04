package design.pattern.behavioral_patterns.mediator.location;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 21:57:32
 * @desc 县城/区
 */
@Data
public class CountryTown {
	// 城市id
	private String cityId;
	// 县城id
	private String townId;
	// 县城名称
	private String townName;
}
