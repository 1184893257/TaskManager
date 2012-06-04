package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import data.task.DayTask;

/**
 * �������һ�������<br>
 * �ж��DayTask
 * 
 * @author lqy
 * 
 */
public class Day extends TaskMap<DayTask> {
	/**
	 * ��������ָʾ��·����ȡ��������Ĺ��췽��
	 * 
	 * @param year
	 *            ��
	 * @param month
	 *            ��
	 * @param day
	 *            ��
	 */
	protected Day(int year, int month, int day) {
		dir = DATA + File.separator + year + File.separator + month;
		path = dir + File.separator + day;
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, DayTask>();
	}

	/**
	 * ����һ�������ļ���
	 * 
	 * @param cal
	 *            ���������
	 * @param bringYesterday
	 *            �Ƿ�ת����������񵽽���
	 */
	protected Day(Calendar cal, boolean bringYesterday) {
		this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH), cal
				.get(Calendar.DATE));
		// Ҫת������δ��ɵ����񵽽�����?
		if (bringYesterday) {
			Calendar yesterday = (Calendar) cal.clone();// �����޸��ⲿ���������
			yesterday.add(Calendar.DATE, -1);
			Day y = new Day(yesterday, false);

			// ѭ��ת������δ��ɵ����񵽽���
			Iterator<Entry<String, DayTask>> it = y.iterator();
			// ת�ƹ�������,ת��֮���������ɾ��
			LinkedList<String> trans = new LinkedList<String>();
			DayTask d;
			while (it.hasNext()) {
				d = it.next().getValue();
				if (!d.finished) {
					tasks.put(d.info, d);
					trans.add(d.info);
				}
			}

			// ɾ��ת�ƹ�������
			for (String task : trans)
				y.tasks.remove(task);
			y.writeTasks();
		}
	}

	/**
	 * �ṩ�ⲿʹ�õ�һ������񼯺ϵĹ��췽��
	 * 
	 * @param cal
	 *            ����
	 */
	public Day(Calendar cal) {
		this(cal, true);
	}

	/**
	 * ��ý����������������ʱ��<br>
	 * ����������������ʱ��û�������,�������������д��TaskMap������
	 * 
	 * @return �����������������ʱ��
	 */
	public long getTotal() {
		Iterator<Entry<String, DayTask>> it = tasks.entrySet().iterator();
		long ans = 0L;
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			ans += task.finished ? task.lastTime : task.needTime;
		}
		return ans;
	}

	/**
	 * ��ý����Ѿ���ɵ��������ʱ��
	 * 
	 * @return �����Ѿ���ɵ��������ʱ��
	 */
	public long getFinished() {
		long ans = 0L;
		Iterator<Entry<String, DayTask>> it = tasks.entrySet().iterator();
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			ans += task.lastTime;
		}
		return ans;
	}

	@Override
	public void buildDir(String dir) {
		this.dir = dir;
	}
}
