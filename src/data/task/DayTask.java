package data.task;

public class DayTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * ����һ��һ�������
	 * 
	 * @param info
	 *            ���������
	 * @param needTime
	 *            ���������Ҫ��ʱ��(��λ:����)
	 */
	public DayTask(String info, long needTime) {
		this.info = info;
		this.needTime = needTime;
	}

}
