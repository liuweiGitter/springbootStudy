package design.pattern.behavioral_patterns.memento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liuwei
 * @date 2019-08-05 23:14:15
 * @desc 备忘录类：存储所需的属性字段
 */
public class MementoEntity {
	private String state;
	private LocalDateTime time;

	public MementoEntity(String state, LocalDateTime time) {
		this.state = state;
		this.time = time;
	}

	public String getState() {
		return state;
	}

	public LocalDateTime getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "MementoEntity [state=" + state + ", time=" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")) + "]";
	}
	
}
