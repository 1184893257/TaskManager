package data.task;

public class MonthTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * ����һ������һ���µ�����
	 * 
	 * @param info
	 *            ������
	 * @param father
	 *            ������
	 */
	public MonthTask(String info, String father) {
		super(info, 0L, father);
	}

}
