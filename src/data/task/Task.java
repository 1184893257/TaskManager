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
}
