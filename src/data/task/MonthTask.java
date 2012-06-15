package data.task;

public class MonthTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造一个持续一个月的任务
	 * 
	 * @param info
	 *            任务名
	 * @param father
	 *            父任务
	 */
	public MonthTask(String info, String father) {
		super(info, 0L, father);
	}

}
