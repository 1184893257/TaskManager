package data.task;

public class DayTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * 此任务可属于于一个周任务<br>
	 * 为null表示不属于任何一个周任务
	 */
	public String father;

	/**
	 * 构造一个一天的任务
	 * 
	 * @param info
	 *            任务的内容
	 * @param needTime
	 *            任务估计需要的时间(单位:毫秒)
	 */
	public DayTask(String info, long needTime) {
		super(DAYTASK);

		this.info = info;
		this.needTime = needTime;
	}

	/**
	 * 采用默认所需时间的构造函数
	 * 
	 * @param info
	 *            任务内容
	 */
	public DayTask(String info) {
		this(info, 3 * 60 * 1000);// 若是没指定所需时间那就默认3分钟
	}

	@Override
	public void add(long time) {
		this.lastTime += time;
		if (null != father) {
			// XXX thismonth中的father要增加
		}
	}

}
