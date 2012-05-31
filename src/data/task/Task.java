package data.task;

import java.io.Serializable;

/**
 * 此类是各种任务的父类
 * 
 * @author lqy
 * 
 */
public abstract class Task implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int DAYTASK = 0; // 一天的任务
	public static final int WEEKTASK = 1; // 一周任务
	public static final int MONTHTASK = 2; // 一月的任务
	public static final int YEARTASK = 3; // 一年的任务

	/**
	 * 任务的类型<br>
	 * 有一天的任务,一周的任务,一月的任务,一年的任务
	 */
	public int type;
	/**
	 * 任务的内容<br>
	 * 也用于区分同一级的不同任务
	 */
	public String info;
	/**
	 * 预期任务所需时间(可选)
	 */
	public long needTime;
	/**
	 * 任务是否完成
	 */
	public boolean finished;
	/**
	 * 任务持续时间<br>
	 * 如果任务已完成则是任务实际所花时间
	 */
	public long lastTime;

	/**
	 * 构造任务
	 * 
	 * @param type
	 *            任务的类型
	 */
	public Task(int type) {
		this.type = type;
	}
}
