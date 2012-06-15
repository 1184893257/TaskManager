package data.task;

import java.io.Serializable;

/**
 * �����Ǹ�������ĸ���
 * 
 * @author lqy
 * 
 */
public abstract class Task implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * ���������<br>
	 * Ҳ��������ͬһ���Ĳ�ͬ����
	 */
	public String info;
	/**
	 * �������ֱ���ϲ�����
	 */
	public String father;
	/**
	 * Ԥ����������ʱ��
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
	 * �����������
	 * 
	 * @param info
	 *            ���������,����ַ�����ͬһ����Ψһ��,����Ψһ�����������
	 * @param needTime
	 *            Ԥ������ʱ��
	 * @param father
	 *            �����������
	 */
	public Task(String info, long needTime, String father) {
		this.info = info;
		this.needTime = needTime;
		this.father = father;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
