package data.task;

import java.io.Serializable;

/**
 * 此类是各种任务的父类
 * 
 * @author lqy
 * 
 */
public abstract class Task implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * 任务的内容<br>
	 * 也用于区分同一级的不同任务
	 */
	public String info;
	/**
	 * 此任务的直接上层任务
	 */
	public String father;
	/**
	 * 预期任务所需时间
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
	 * 构造任务对象
	 * 
	 * @param info
	 *            任务的名称,这个字符串是同一域中唯一的,用于唯一索引任务对象
	 * @param needTime
	 *            预计所需时间
	 * @param father
	 *            父任务的名称
	 */
	public Task(String info, long needTime, String father) {
		this.info = info;
		this.needTime = needTime;
		this.father = father;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
