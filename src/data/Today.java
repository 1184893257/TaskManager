package data;

import java.util.Calendar;
import java.util.Date;

import data.task.DayTask;
import data.tasks.Day;

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
	public Day tasks;
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

		// 日任务集合的构造
		tasks = new Day(date);
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
		DayTask task = tasks.get(cur);
		tasks.addLastTime(cur, now.getTime() - begin.getTime());
		tasks.finish(cur);
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
		used = tasks.get(cur).lastTime;

		// 从现在起工作了,把前一段空闲的时间加入到空闲时间中
		vacancy += now.getTime() - startLazy.getTime();
	}

	/**
	 * 中断当前任务<br>
	 * 暂停现在的任务
	 * 
	 * @param now
	 *            现在的时间
	 */
	public void stopTask(Date now) {
		tasks.addLastTime(cur, now.getTime() - begin.getTime());
		cur = null;

		// 从现在开始又空闲了
		startLazy = now;
	}
}
