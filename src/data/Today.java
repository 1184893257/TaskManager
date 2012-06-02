package data;

import java.util.Date;

import data.tasks.Day;

/**
 * ����ʱ����
 * 
 * @author lqy
 * 
 */
public class Today {
	/**
	 * ������Ŀ���ʱ��
	 */
	public long startup;

	/**
	 * ���յ�����
	 */
	public Day tasks;
	/**
	 * ָ����ǰ����ı��<br>
	 * Ϊnull��ʾ��ǰ������
	 */
	public String cur;
	/**
	 * ��ǰ�����ڼ���֮ǰ����ʱ��
	 */
	public long used;
	/**
	 * ��ǰ����ļ���ʱ��
	 */
	public Date begin;

	/**
	 * ����ʱ��
	 */
	public long vacancy;
	/**
	 * ���ÿ�ʼ
	 */
	public Date startLazy;

	public Today() {
		Date now = new Date();
		startup = now.getTime();
		startLazy = now;
		tasks = new Day();
	}

	/**
	 * ��ó��������ʱ��
	 * 
	 * @return ��������ʱ��UTSʱ��(��λ:����)
	 */
	public long getStartTime() {
		return startup;
	}

	/**
	 * ��ǰ�ǲ�������ִ������
	 * 
	 * @return
	 */
	public boolean isWorking() {
		return null != cur;
	}

	/**
	 * ��õ�ǰ��������ʱ��
	 * 
	 * @return ��ǰ����(��λ:����)
	 */
	public long getCurUsed() {
		return used + new Date().getTime() - begin.getTime();
	}
}
