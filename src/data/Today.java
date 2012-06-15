package data;

import java.util.*;

import data.task.DayTask;
import data.tasks.*;

/**
 * 运行时数据
 * 
 * @author lqy
 * 
 */
public class Today {
	/**
	 * 本程序的开启时刻
	 */
	public long startup;

	/**
	 * 今日的任务
	 */
	public Day day;
	/**
	 * 本周的任务
	 */
	public Week week;
	/**
	 * 本月的任务
	 */
	public Month month;
	/**
	 * 本年的任务
	 */
	public Year year;
	/**
	 * 指代当前任务的标记<br>
	 * 为null表示当前无任务
	 */
	public String cur;
	/**
	 * 当前任务在激活之前已用时间
	 */
	public long used;
	/**
	 * 当前任务的激活时刻
	 */
	public Date begin;

	/**
	 * 空置时间
	 */
	public long vacancy;
	/**
	 * 空置开始
	 */
	public Date startLazy;

	public Today() {
		Date now = new Date();
		startup = now.getTime();
		startLazy = now;

		Calendar date = Calendar.getInstance();
		date.setTime(now);

		// 读出年月周日各级任务集合
		year = Year.newYear(date);
		month = Month.newMonth(date, year);
		week = Week.newWeek(date, month);
		day = Day.newDay(date, week);
	}

	/**
	 * 获得程序的启动时间
	 * 
	 * @return 程序启动时的UTS时间(单位:毫秒)
	 */
	public long getStartTime() {
		return startup;
	}

	/**
	 * 当前是不是正在执行任务
	 * 
	 * @return
	 */
	public boolean isWorking() {
		return null != cur;
	}

	/**
	 * 获得当前任务已用时间
	 * 
	 * @return 当前任务(单位:毫秒)
	 */
	public long getCurUsed() {
		return used + new Date().getTime() - begin.getTime();
	}

	/**
	 * 完成当前任务
	 * 
	 * @param now
	 *            现在的时间
	 * @return 当前结束的任务最终所用的时间(单位:毫秒)
	 */
	public long finishCur(Date now) {
		DayTask task = day.get(cur);
		day.addLastTime(cur, now.getTime() - begin.getTime());
		day.finish(cur);
		cur = null; // 现在没任务

		// 从现在起空闲了
		startLazy = now;
		return task.needTime;
	}

	/**
	 * 开启一个新任务
	 * 
	 * @param task
	 *            任务内容
	 * @param now
	 *            现在的时间
	 */
	public void startTask(String task, Date now) {
		cur = task;
		begin = now;
		used = day.get(cur).lastTime;

		// 从现在起工作了,把前一段空闲的时间加入到空闲时间中
		vacancy += now.getTime() - startLazy.getTime();
	}

	/**
	 * 中断当前任务<br>
	 * 暂停现在的任务
	 * 
	 * @param now
	 *            现在的时间
	 * @return 当前任务的所需时间可能已经变更
	 */
	public long stopTask(Date now) {
		DayTask task = day.get(cur);
		day.addLastTime(cur, now.getTime() - begin.getTime());
		cur = null;

		// 从现在开始又空闲了
		startLazy = now;
		return task.needTime;
	}
}
