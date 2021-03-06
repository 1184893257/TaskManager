package data.tasks;

import java.io.File;
import java.util.*;

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
			this.bringLast(lastMonth);
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
		return newMonth(cal, father, true);
	}

	public static Month newMonth(Calendar cal, Year father,
			boolean bringLastWeek) {
		Month ans = new Month(cal, bringLastWeek);
		ans.father = father;
		return ans;
	}

	@Override
	public TreeMap<String, Calendar> getBrothers(Calendar cal) {
		TreeMap<String, Calendar> ans = new TreeMap<String, Calendar>();
		int year = cal.get(Calendar.YEAR);
		for (int month = 0; month < 12; ++month) {
			Calendar cal2 = (Calendar) cal.clone();
			cal2.set(year, month, 1);
			ans.put(this.getItemByCal(cal2), cal2);
		}
		return ans;
	}

	@Override
	public String getItemByCal(Calendar cal) {
		return String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + " 月";
	}

	@Override
	public String getPanelBorder(Calendar cal) {
		return cal.get(Calendar.YEAR) + " 年 " + this.getItemByCal(cal);
	}

	@Override
	public Calendar firstDay(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		c.set(Calendar.DATE, 1);
		return c;
	}

	@Override
	public Calendar lastDay(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c;
	}

}
