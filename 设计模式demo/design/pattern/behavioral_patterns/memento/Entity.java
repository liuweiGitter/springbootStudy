package design.pattern.behavioral_patterns.memento;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-08-05 23:08:30
 * @desc 实体类
 * 其中的一些字段需要保存历史赋值记录，以便在需要的时候恢复数据
 * 备忘历史状态的应用场景很多，但在java中比较少见
 */
@Data
public class Entity {
	private String state;

	// 保存状态
	public MementoEntity saveStateToMemento() {
		return new MementoEntity(state, LocalDateTime.now());
	}

	// 恢复状态
	public void getStateFromMemento(MementoEntity memento) {
		state = memento.getState();
	}
}
