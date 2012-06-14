package data;

import java.util.Calendar;
import java.util.Date;

import data.task.DayTask;
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

		Calendar date = Calendar.getInstance();
		date.setTime(now);

		// �����񼯺ϵĹ���
		tasks = new Day(date);
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

	/**
	 * ��ɵ�ǰ����
	 * 
	 * @param now
	 *            ���ڵ�ʱ��
	 * @return ��ǰ�����������������õ�ʱ��(��λ:����)
	 */
	public long finishCur(Date now) {
		DayTask task = tasks.get(cur);
		tasks.addLastTime(cur, now.getTime() - begin.getTime());
		tasks.finish(cur);
		cur = null; // ����û����

		// �������������
		startLazy = now;
		return task.needTime;
	}

	/**
	 * ����һ��������
	 * 
	 * @param task
	 *            ��������
	 * @param now
	 *            ���ڵ�ʱ��
	 */
	public void startTask(String task, Date now) {
		cur = task;
		begin = now;
		used = tasks.get(cur).lastTime;

		// ������������,��ǰһ�ο��е�ʱ����뵽����ʱ����
		vacancy += now.getTime() - startLazy.getTime();
	}

	/**
	 * �жϵ�ǰ����<br>
	 * ��ͣ���ڵ�����
	 * 
	 * @param now
	 *            ���ڵ�ʱ��
	 */
	public void stopTask(Date now) {
		tasks.addLastTime(cur, now.getTime() - begin.getTime());
		cur = null;

		// �����ڿ�ʼ�ֿ�����
		startLazy = now;
	}
}
