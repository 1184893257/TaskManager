package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import data.task.*;

public class Month extends TaskMap<MonthTask, YearTask> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * 使用年月构造月任务集合的存储路径
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 */
	protected Month(int year, int month) {
		dir = DATA + File.separator + year + File.separator + month;
		path = dir + File.separator + "month";
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, MonthTask>();
	}

	/**
	 * 根据日期构造月任务集合
	 * 
	 * @param cal
	 *            日期
	 * @param bringLastMonth
	 *            是否转移上月的任务到这个月?
	 */
	protected Month(Calendar cal, boolean bringLastMonth) {
		this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH));
		if (bringLastMonth) {
			// 读出上个月的月任务集合
			Calendar cal2 = (Calendar) cal.clone();
			cal2.add(Calendar.MONTH, -1);
			Month lastMonth = new Month(cal2, false);

			// 循环转移上月未完成的任务
			Iterator<Entry<String, MonthTask>> it = lastMonth.iterator();
			MonthTask d;
			while (it.hasNext()) {
				d = it.next().getValue();
				if (!d.finished) {
					tasks.put(d.info, d);
					it.remove();
				}
			}
			this.writeTasks();// 把这周的修改写入
			lastMonth.writeTasks();// 把上周的修改写入
		}
	}

	/**
	 * 提供给外部使用的用日历构造月任务集合的静态方法
	 * 
	 * @param cal
	 *            日期
	 * @param father
	 *            年任务集合
	 * @return cal所在月的月任务集合
	 */
	public static Month newMonth(Calendar cal, Year father) {
		Month ans = new Month(cal, true);
		ans.father = father;
		return ans;
	}

}
