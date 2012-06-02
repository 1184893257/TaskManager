package data.tasks;

import java.util.*;
import java.util.Map.Entry;

import data.task.DayTask;

/**
 * 此类包含一天的任务<br>
 * 有多个DayTask
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
	 * 获得今天的所有任务所需时间<br>
	 * 对于周月年总任务时间没多大意义,所以这个方法不写在TaskMap父类中
	 * 
	 * @return 今天的所有任务所需时间
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
	 * 获得今天已经完成的任务的总时间
	 * 
	 * @return 今天已经完成的任务的总时间
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
