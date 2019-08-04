package design.pattern.behavioral_patterns.mediator.location;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-03 21:52:02
 * @desc 省
 */
@Data
public class Province {
	//国家id
	private String countryId;
	//省id
	private String provinceId;
	//省名称
	private String provinceName;
}
