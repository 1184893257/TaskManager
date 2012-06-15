package data.task;

public class WeekTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造一个持续一周的任务
	 * 
	 * @param info
	 *            任务名
	 * @param father
	 *            父任务名
	 */
	public WeekTask(String info, String father) {
		super(info, 0L, father);
	}
}
