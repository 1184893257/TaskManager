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
	 * 任务所花时间增长一段<br>
	 * 可能需要调用上级的add
	 * 
	 * @param time
	 *            花费的一段时间(单位:毫秒)
	 */
	public abstract void add(long time);

	/**
	 * 任务完成
	 */
	public void finished() {
		this.finished = true;
	}
}
