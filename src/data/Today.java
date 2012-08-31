package data;

import java.util.*;

import data.task.DayTask;
import data.tasks.*;

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
	public Day day;
	/**
	 * ���������
	 */
	public Day tomorrow;
	/**
	 * ���ܵ�����
	 */
	public Week week;
	/**
	 * ���µ�����
	 */
	public Month month;
	/**
	 * ���������
	 */
	public Year year;
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

	public Today(Calendar date) {
		Date now = date.getTime();
		startup = now.getTime();
		startLazy = now;

		// �����������ո������񼯺�
		year = Year.newYear(date);
		month = Month.newMonth(date, year);
		week = Week.newWeek(date, month);
		day = Day.newDay(date, week);

		// ��ʼ��tomorrow,��ɱ�today���Ӱ�O(��_��)O
		Calendar tom = (Calendar) date.clone();
		tom.add(Calendar.DATE, 1);

		// ��3�����ܸ�year\month\weekһ��Ŷ
		Year tomYear;
		Month tomMonth;
		Week tomWeek;

		// ��������������ͬһ����ô�Ͳ��ô������˺���ͬ��
		tomYear = tom.get(Calendar.YEAR) == date.get(Calendar.YEAR) ? year
				: Year.newYear(tom, false);
		tomMonth = tom.get(Calendar.MONTH) == date.get(Calendar.MONTH) ? month
				: Month.newMonth(tom, tomYear, false);

		Calendar temp = (Calendar) tom.clone();
		Week.firstDayofWeek(temp);
		tomWeek = temp.after(date) ? Week.newWeek(temp, tomMonth, false) : week;

		tomorrow = Day.newDay(tom, tomWeek, false);
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
		DayTask task = day.get(cur);
		day.addLastTime(cur, now.getTime() - begin.getTime());
		day.finish(cur);
		cur = null; // ����û����

		// �������������
		startLazy = now;
		return task.needTime;
	}

	/**
	 * �������task,��������ʱ��Ϊtime
	 * 
	 * @param task
	 *            ������
	 * @param time
	 *            ����ʱ��
	 */
	public void finish(String task, long time) {
		day.addLastTime(task, time - day.get(task).lastTime);
		day.finish(task);
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
		used = day.get(cur).lastTime;

		// ������������,��ǰһ�ο��е�ʱ����뵽����ʱ����
		vacancy += now.getTime() - startLazy.getTime();
	}

	/**
	 * �жϵ�ǰ����<br>
	 * ��ͣ���ڵ�����
	 * 
	 * @param now
	 *            ���ڵ�ʱ��
	 * @return ��ǰ���������ʱ������Ѿ����
	 */
	public long stopTask(Date now) {
		DayTask task = day.get(cur);
		day.addLastTime(cur, now.getTime() - begin.getTime());
		cur = null;

		// �����ڿ�ʼ�ֿ�����
		startLazy = now;
		return task.needTime;
	}
}
