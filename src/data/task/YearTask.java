package data.task;

public class YearTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造一个持续一年的任务
	 * 
	 * @param info
	 *            任务名
	 */
	public YearTask(String info) {
		super(info, 0L, null);
	}

}
