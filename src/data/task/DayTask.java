package data.task;

public class DayTask extends Task {
	private static final long serialVersionUID = 1L;

	/**
	 * �������������һ��������<br>
	 * Ϊnull��ʾ�������κ�һ��������
	 */
	public String father;

	/**
	 * ����һ��һ�������
	 * 
	 * @param info
	 *            ���������
	 * @param needTime
	 *            ���������Ҫ��ʱ��(��λ:����)
	 */
	public DayTask(String info, long needTime) {
		super(DAYTASK);

		this.info = info;
		this.needTime = needTime;
	}

	/**
	 * ����Ĭ������ʱ��Ĺ��캯��
	 * 
	 * @param info
	 *            ��������
	 */
	public DayTask(String info) {
		this(info, 3 * 60 * 1000);// ����ûָ������ʱ���Ǿ�Ĭ��3����
	}

	@Override
	public void add(long time) {
		this.lastTime += time;
		if (null != father) {
			// XXX thismonth�е�fatherҪ����
		}
	}

}
