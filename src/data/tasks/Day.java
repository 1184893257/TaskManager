package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import data.StaticData;
import data.task.*;

import static gui.FormatTime.*;

/**
 * 此类包含一天的任务<br>
 * 有多个DayTask
 * 
 * @author lqy
 */
public class Day extends TaskMap<DayTask, WeekTask> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * 根据日期指示的路径读取今天任务的构造方法
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 */
	protected Day(int year, int month, int day) {
		dir = DATA + File.separator + year + File.separator + month;
		path = dir + File.separator + day;
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, DayTask>();
	}

	/**
	 * 建立一天的任务的集合
	 * 
	 * @param cal
	 *            今天的日期
	 * @param bringYesterday
	 *            是否转移昨天的任务到今天
	 */
	protected Day(Calendar cal, boolean bringYesterday) {
		this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH), cal
				.get(Calendar.DATE));
		// 要转移昨天未完成的任务到今天吗?
		if (bringYesterday) {
			Calendar yesterday = (Calendar) cal.clone();// 不能修改外部传入的日期
			yesterday.add(Calendar.DATE, -1);
			Day y = new Day(yesterday, false);

			// 循环转移昨天未完成的任务到今天
			Iterator<Entry<String, DayTask>> it = y.iterator();
			DayTask d;
			while (it.hasNext()) {
				d = it.next().getValue();
				if (!d.finished) {
					tasks.put(d.info, d);
					it.remove();
				}
			}
			this.writeTasks();// 把今天的修改写入
			y.writeTasks();// 把昨天的修改写入
		}
	}

	/**
	 * 因为Week构造前需对日历对象做些处理,而构造方法中前期能做的工作有限,<br>
	 * 所以统一 使用静态方法来生成对象
	 * 
	 * @param cal
	 *            日期
	 * @return 天任务集合
	 */
	public static Day newDay(Calendar cal, Week father) {
		Day d = new Day(cal, true);
		d.father = father;
		return d;
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
			ans += task.needTime;
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

	@Override
	public boolean add(DayTask e) {
		if (!super.add(e)) // 如果任务名重名这里就返回false
			return false;
		if (e.father != null) {
			father.addNeedTime(e.father, e.needTime);
			father.addLastTime(e.father, e.lastTime);
		}
		return true;
	}

	@Override
	protected boolean remove(String info, String type) {
		DayTask task = tasks.remove(info);
		this.writeTasks();// 已修改,保存修改
		if (task.father != null) {
			father.addLastTime(task.father, -task.lastTime);
			father.addNeedTime(task.father, -task.needTime);
		}
		return true;
	}

	@Override
	public void addLastTime(String info, long time) {
		DayTask task = tasks.get(info);
		if (task.lastTime + time > task.needTime) {// 直接加入这段时间就超了
			long scale = (long) ((task.lastTime + time) * (1.0 + StaticData.INCRATE))
					- task.needTime;
			addNeedTime(info, scale); // 自适应扩展所需时间
		}
		super.addLastTime(info, time);
	}

	/**
	 * 递归的完成所有所需时间==已用时间的任务info及其父任务
	 * 
	 * @param map
	 *            任务集合
	 * @param info
	 *            任务
	 */
	protected <E extends Task, K extends Task> void finish(TaskMap<E, K> map,
			String info) {
		String up; // 上一级的任务
		Task task = map.get(info);
		if (task != null)
			if (task.needTime > task.lastTime)// 这一级任务还有子任务未完成
				return;
			else {
				task.finished = true;
				up = task.father;
				this.writeTasks();// 任务标志有修改,保存
			}
		else
			up = info;

		if (up != null)
			finish(map.father, task.father);
	}

	/**
	 * 完成任务info
	 * 
	 * @param info
	 *            能代表要完成的任务的字符串
	 */
	public void finish(String info) {
		DayTask task = tasks.get(info);
		long scale = task.lastTime - task.needTime;// 一般是负数
		addNeedTime(info, scale);// 收缩所需时间为最终花费的时间
		finish(this, info);
	}

	public static void main(String[] args) {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		Calendar t = (Calendar) today.clone();
		t.set(2012, 6 - 1, 7);

		while (t.before(today)) {
			Day aday = new Day(t, false);
			System.out.println(t.getTime());
			Iterator<Entry<String, DayTask>> it = aday.iterator();
			while (it.hasNext()) {
				DayTask task = it.next().getValue();
				System.out.println(task.info + " " + HMS(task.needTime) + "-"
						+ HMS(task.lastTime));
				if (task.finished)
					task.needTime = task.lastTime;
			}
			aday.writeTasks();
			System.out.println();
			t.add(Calendar.DATE, 1);
		}
	}
}
