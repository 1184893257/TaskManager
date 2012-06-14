package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import data.StaticData;
import data.task.*;

/**
 * �������һ�������<br>
 * �ж��DayTask
 * 
 * @author lqy
 */
public class Day extends TaskMap<DayTask, Task> {// TODO KӦ����Week
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
			this.writeTasks();// �ѽ�����޸�д��

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
	public Day(Calendar cal) {// TODO �����ʱ�����ϼ�
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
			ans += task.needTime;
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

	@Override
	public boolean add(DayTask e) {
		if (!super.add(e)) // �����������������ͷ���false
			return false;
		if (e.father != null) {
			father.addNeedTime(e.father, e.needTime);
			father.addLastTime(e.father, e.lastTime);
		}
		return true;
	}

	@Override
	protected boolean remove(String info, String type) {
		DayTask task = tasks.remove(info);
		this.writeTasks();// ���޸�,�����޸�
		if (task.father != null) {
			father.addLastTime(task.father, -task.lastTime);
			father.addNeedTime(task.father, -task.needTime);
		}
		return true;
	}

	@Override
	public void addLastTime(String info, long time) {
		DayTask task = tasks.get(info);
		if (task.lastTime + time > task.needTime) {// ֱ�Ӽ������ʱ��ͳ���
			long scale = (long) ((task.lastTime + time) * (1.0 + StaticData.INCRATE))
					- task.needTime;
			addNeedTime(info, scale); // ����Ӧ��չ����ʱ��
		}
		super.addLastTime(info, time);
	}

	/**
	 * �ݹ�������������ʱ��==����ʱ�������info���丸����
	 * 
	 * @param map
	 *            ���񼯺�
	 * @param info
	 *            ����
	 */
	protected <E extends Task, K extends Task> void finish(TaskMap<E, K> map,
			String info) {
		String up; // ��һ��������
		Task task = map.get(info);
		if (task != null)
			if (task.needTime > task.lastTime)// ��һ��������������δ���
				return;
			else {
				task.finished = true;
				up = task.father;
				this.writeTasks();// �����־���޸�,����
			}
		else
			up = info;

		if (up != null)
			finish(map.father, task.father);
	}

	/**
	 * �������info
	 * 
	 * @param info
	 *            �ܴ���Ҫ��ɵ�������ַ���
	 */
	public void finish(String info) {
		DayTask task = tasks.get(info);
		long scale = task.lastTime - task.needTime;// һ���Ǹ���
		addNeedTime(info, scale);// ��������ʱ��Ϊ���ջ��ѵ�ʱ��
		finish(this, info);
	}
}
