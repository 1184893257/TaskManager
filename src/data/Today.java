package data;

import java.util.Date;

import data.tasks.Day;

public class Today {
	/**
	 * ������Ŀ���ʱ��
	 */
	protected long startup;

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
	protected long used;
	/**
	 * ��ǰ����ļ���ʱ��
	 */
	protected Date begin;

	public Today() {
		startup = new Date().getTime();
		tasks=new Day();
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
