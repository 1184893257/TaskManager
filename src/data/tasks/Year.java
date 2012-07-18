package data.tasks;

import java.io.File;
import java.util.*;

import data.task.*;

public class Year extends TaskMap<YearTask, Task> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * 使用年份构造年任务集合
	 * 
	 * @param year
	 *            年
	 */
	protected Year(int year) {
		dir = DATA + File.separator + year;
		path = dir + File.separator + "year";
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, YearTask>();
	}

	/**
	 * 使用日期构造年任务集合
	 * 
	 * @param cal
	 *            日期
	 * @param bringLastYear
	 *            是否转移去年未完成的年任务到今年
	 */
	protected Year(Calendar cal, boolean bringLastYear) {
		this(cal.get(Calendar.YEAR));
		if (bringLastYear) {
			// 读出去年的年任务集合
			Calendar cal2 = (Calendar) cal.clone();
			cal2.add(Calendar.YEAR, -1);
			Year lastYear = new Year(cal2, false);
			this.bringLast(lastYear);
		}
	}

	/**
	 * 向外部提供的根据日期所在年构造年任务集合的静态方法
	 * 
	 * @param cal
	 *            日期
	 * @return cal所在年的年任务集合
	 */
	public static Year newYear(Calendar cal) {
		return newYear(cal, true);
	}

	public static Year newYear(Calendar cal, boolean bringLastYear) {
		return new Year(cal, bringLastYear);
	}

	@Override
	public TreeMap<String, Calendar> getBrothers(Calendar cal) {
		TreeMap<String, Calendar> ans = new TreeMap<String, Calendar>();
		File[] folders = new File(DATA).listFiles();

		// 将data文件夹中所有已建立的年份加入到ans中
		for (File e : folders) {
			String name = e.getName();
			if (e.isDirectory() && name.matches("\\d+")) {
				Calendar cal2 = (Calendar) cal.clone();
				cal2.set(Integer.parseInt(name), 0, 1);
				ans.put(this.getItemByCal(cal2), cal2);
			}
		}

		// 再添加一个大一号的年份
		int biggest = ans.lastEntry().getValue().get(Calendar.YEAR);
		Calendar cal3 = (Calendar) cal.clone();
		cal3.set(biggest + 1, 0, 1);
		ans.put(this.getItemByCal(cal3), cal3);
		return ans;
	}

	@Override
	public String getItemByCal(Calendar cal) {
		return cal.get(Calendar.YEAR) + " 年";
	}

	@Override
	public String getPanelBorder(Calendar cal) {
		return this.getItemByCal(cal);
	}
}
