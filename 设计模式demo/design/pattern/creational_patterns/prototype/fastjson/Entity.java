package design.pattern.creational_patterns.prototype.fastjson;

import design.pattern.creational_patterns.prototype.clone.Helper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-28 17:23:48
 * @desc
 */
@Slf4j
public class Entity {
	public String stringValue = "5";
	public int basicValue = 5;
	public Helper objectValue = new Helper();

	public void printMsg(String preffix) {
		if (null == preffix) {
			preffix = "";
		}
		log.info(preffix + "hashcode:" + hashCode() + ",basicValue:" + basicValue + ",stringValue:" + stringValue
				+ ",objectValue:" + objectValue);
	}

}
