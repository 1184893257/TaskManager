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
}
