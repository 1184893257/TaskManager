package data.tasks;

import java.util.*;
import java.util.Map.Entry;

import data.task.DayTask;

/**
 * �������һ�������<br>
 * �ж��DayTask
 * 
 * @author lqy
 * 
 */
public class Day extends TaskMap<DayTask> {

	public Day(int year, int month, int day) {
	}

	public Day() {
		path = "today";
		if (!this.readTasks()) {
			tasks = new TreeMap<String, DayTask>();
		}
	}

	/**
	 * ��ý����������������ʱ��<br>
	 * ����������������ʱ��û�������,�������������д��TaskMap������
	 * 
	 * @return �����������������ʱ��
	 */
	public long getTotal() {
		Iterator<Entry<String, DayTask>> it = tasks.entrySet().iterator();
		long ans = 0L;
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			ans += task.finished ? task.lastTime : task.needTime;
		}
		return ans;
	}

	/**
	 * ��ý����Ѿ���ɵ��������ʱ��
	 * 
	 * @return �����Ѿ���ɵ��������ʱ��
	 */
	public long getFinished() {
		long ans = 0L;
		Iterator<Entry<String, DayTask>> it = tasks.entrySet().iterator();
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			ans += task.lastTime;
		}
		return ans;
	}
}
