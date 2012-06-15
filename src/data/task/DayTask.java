package data.task;

public class DayTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造一个一天的任务
	 * 
	 * @param info
	 *            任务的内容
	 * @param needTime
	 *            任务估计需要的时间(单位:毫秒)
	 * @param father
	 *            父任务
	 */
	public DayTask(String info, long needTime, String father) {
		super(info, needTime, father);
	}

}
