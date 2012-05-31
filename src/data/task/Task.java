package data.task;

import java.io.Serializable;

/**
 * �����Ǹ�������ĸ���
 * 
 * @author lqy
 * 
 */
public abstract class Task implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int DAYTASK = 0; // һ�������
	public static final int WEEKTASK = 1; // һ������
	public static final int MONTHTASK = 2; // һ�µ�����
	public static final int YEARTASK = 3; // һ�������

	/**
	 * ���������<br>
	 * ��һ�������,һ�ܵ�����,һ�µ�����,һ�������
	 */
	public int type;
	/**
	 * ���������<br>
	 * Ҳ��������ͬһ���Ĳ�ͬ����
	 */
	public String info;
	/**
	 * Ԥ����������ʱ��(��ѡ)
	 */
	public long needTime;
	/**
	 * �����Ƿ����
	 */
	public boolean finished;
	/**
	 * �������ʱ��<br>
	 * ��������������������ʵ������ʱ��
	 */
	public long lastTime;

	/**
	 * ��������
	 * 
	 * @param type
	 *            ���������
	 */
	public Task(int type) {
		this.type = type;
	}
}
