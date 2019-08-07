package design.pattern.behavioral_patterns.memento;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 * @date 2019-08-05 23:21:16
 * @desc 备忘录管理类
 */

public class MementoManager {
	private List<MementoEntity> mementoList = new ArrayList<MementoEntity>();

	public void add(MementoEntity state) {
		mementoList.add(state);
	}

	public MementoEntity get(int index) {
		return mementoList.get(index);
	}

	public MementoEntity getFirst() {
		if (mementoList.size() > 0) {
			return mementoList.get(0);
		}
		return null;
	}

	public MementoEntity getLast() {
		int length;
		if ((length = mementoList.size()) > 0) {
			return mementoList.get(length - 1);
		}
		return null;
	}

	public void showTimes() {
		int length;
		if ((length = mementoList.size()) > 0) {
			System.out.print("有"+length+"条保存记录！");
			StringBuilder builder = new StringBuilder();
			DateTimeFormatter formatter = getFormater();
			if (length > 6) {
				System.out.print("\n显示最近的3条记录时间和最初的3条记录时间");
				builder.append("\n[1]" + timeFormate(mementoList.get(0), formatter) + "\n[2]"
						+ timeFormate(mementoList.get(1), formatter))
						.append("\n[3]" + timeFormate(mementoList.get(2), formatter) + "\n[" + (length - 2) + "]"
								+ timeFormate(mementoList.get(length - 3), formatter))
						.append("\n[" + (length - 1) + "]" + timeFormate(mementoList.get(length - 2), formatter) + "\n["
								+ length + "]" + timeFormate(mementoList.get(length - 1), formatter));
			} else {
				for (int i = 0; i < length; i++) {
					builder.append("\n[" + (i + 1) + "]" + timeFormate(mementoList.get(i), formatter));
				}
			}
			System.out.println(builder.toString());
		} else {
			System.out.println("无保存记录！");
		}
	}

	private DateTimeFormatter getFormater() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
	}

	private String timeFormate(MementoEntity mementoEntity, DateTimeFormatter formatter) {
		return mementoEntity.getTime().format(formatter);
	}
}
