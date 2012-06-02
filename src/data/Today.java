package data;

import java.util.Date;

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
		tasks = new Day();
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
}
