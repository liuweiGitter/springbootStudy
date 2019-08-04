package design.pattern.behavioral_patterns.mediator.group;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-04 00:02:06
 * @desc 群主题类型
 */
@Data
public class GroupThemeType {
	//群主题类型id
	private String themeId;
	//群主题类型名称
	private String themeName;
	//群主题父主题类型id
	private String parentThemeId;
}
