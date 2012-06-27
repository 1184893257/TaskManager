package data.tasks;

import java.io.File;
import java.util.*;
import data.task.*;

public class Week extends TaskMap<WeekTask, MonthTask> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * 根据本周的第一天的日期构造出周任务的存放路径,并尝试读出任务集合
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 */
	protected Week(int year, int month, int day) {
		dir = DATA + File.separator + year + File.separator + month;
		path = dir + File.separator + "w" + day;
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, WeekTask>();
	}

	/**
	 * <p>
	 * 将cal修改为cal所在周的第一天
	 * </p>
	 * 如果这周的周一到cal都在一个月中,那么cal将被修改为这周的星期一, 否则cal被修改为这个月的1号
	 * 
	 * @param cal
	 *            要修改的日期,这个对象可能会改动
	 */
	public static void firstDayofWeek(Calendar cal) {
		Calendar cal2 = (Calendar) cal.clone();

		int days;// 减days天可到达这个周的周一
		days = cal.get(Calendar.DAY_OF_WEEK);
		days = days == Calendar.SUNDAY ? 6 : days - 2;

		cal2.add(Calendar.DATE, -days);
		if (cal2.get(Calendar.MONTH) == cal.get(Calendar.MONTH))// 周一在这个月里
			cal.add(Calendar.DATE, -days);
		else
			cal.set(Calendar.DATE, 1);
	}

	/**
	 * 根据日期cal构造周任务集合
	 * 
	 * @param cal
	 *            日期<b>必须是一周的第一天</b>
	 * @param bringLastWeek
	 *            要转移上一周未完成的任务到这周吗?
	 */
	protected Week(Calendar cal, boolean bringLastWeek) {
		this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH), cal
				.get(Calendar.DATE));
		if (bringLastWeek) {
			// 读出上个周的周任务集合
			cal.add(Calendar.DATE, -1);
			firstDayofWeek(cal);
			Week lastWeek = new Week(cal, false);
			this.bringLast(lastWeek);
		}
	}

	/**
	 * 提供给外部使用的周任务集合的构造方法
	 * 
	 * @param cal
	 *            日期
	 * @param father
	 *            月任务集合
	 * @return cal所在周的周任务集合
	 */
	public static Week newWeek(Calendar cal, Month father) {
		return newWeek(cal, father, true);
	}

	public static Week newWeek(Calendar cal, Month father, boolean bringLastWeek) {
		Calendar cal2 = (Calendar) cal.clone();
		firstDayofWeek(cal2);
		Week ans = new Week(cal2, bringLastWeek);
		ans.father = father;
		return ans;
	}

}
