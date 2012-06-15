package data.task;

public class YearTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造一个持续一年的任务
	 * 
	 * @param info
	 *            任务名
	 * @param father
	 *            父任务
	 */
	public YearTask(String info, String father) {
		super(info, 0L, father);
	}

}
