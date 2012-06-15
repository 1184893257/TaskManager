package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import data.StaticData;
import data.task.*;

import static gui.FormatTime.*;

/**
 * �������һ�������<br>
 * �ж��DayTask
 * 
 * @author lqy
 */
public class Day extends TaskMap<DayTask, WeekTask> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

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
			DayTask d;
			while (it.hasNext()) {
				d = it.next().getValue();
				if (!d.finished) {
					tasks.put(d.info, d);
					it.remove();
				}
			}
			this.writeTasks();// �ѽ�����޸�д��
			y.writeTasks();// ��������޸�д��
		}
	}

	/**
	 * ��ΪWeek����ǰ�������������Щ����,�����췽����ǰ�������Ĺ�������,<br>
	 * ����ͳһ ʹ�þ�̬���������ɶ���
	 * 
	 * @param cal
	 *            ����
	 * @return �����񼯺�
	 */
	public static Day newDay(Calendar cal, Week father) {
		Day d = new Day(cal, true);
		d.father = father;
		return d;
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

	public static void main(String[] args) {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		Calendar t = (Calendar) today.clone();
		t.set(2012, 6 - 1, 7);

		while (t.before(today)) {
			Day aday = new Day(t, false);
			System.out.println(t.getTime());
			Iterator<Entry<String, DayTask>> it = aday.iterator();
			while (it.hasNext()) {
				DayTask task = it.next().getValue();
				System.out.println(task.info + " " + HMS(task.needTime) + "-"
						+ HMS(task.lastTime));
				if (task.finished)
					task.needTime = task.lastTime;
			}
			aday.writeTasks();
			System.out.println();
			t.add(Calendar.DATE, 1);
		}
	}
}
