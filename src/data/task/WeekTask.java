package data.task;

public class WeekTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * ����һ������һ�ܵ�����
	 * 
	 * @param info
	 *            ������
	 * @param father
	 *            ��������
	 */
	public WeekTask(String info, String father) {
		super(info, 0L, father);
	}
}
