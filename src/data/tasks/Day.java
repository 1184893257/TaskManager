package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import data.StaticData;
import data.task.*;

import static gui.StaticMethod.*;
import static data.tasks.Week.*;

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
			this.bringLast(y);
		}
	}

	/**
	 * ��ΪWeek����ǰ�������������Щ����,�����췽����ǰ�������Ĺ�������,<br>
	 * ����ͳһ ʹ�þ�̬���������ɶ���
	 * 
	 * @param cal
	 *            ����
	 * @param father
	 *            �����񼯺�
	 * @return �����񼯺�
	 */
	public static Day newDay(Calendar cal, Week father) {
		return newDay(cal, father, true);
	}

	/**
	 * �����������������񼯺�
	 * 
	 * @param cal
	 *            ����
	 * @param father
	 *            �����񼯺�
	 * @param bringYesterday
	 *            �Ƿ�ת�����������
	 * @return �����񼯺�
	 */
	public static Day newDay(Calendar cal, Week father, boolean bringYesterday) {
		Day d = new Day(cal, bringYesterday);
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
	public boolean isTaskEditable(String info) {
		return !tasks.get(info).finished;// δ��ɵ��������ɾ��
	}

	@Override
	public void remove(String info) {
		DayTask task = tasks.remove(info);
		this.writeTasks();// ���޸�,�����޸�
		if (task.father != null) {
			father.addLastTime(task.father, -task.lastTime);
			father.addNeedTime(task.father, -task.needTime);
		}
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
				map.writeTasks();// �����־���޸�,����
			}
		else
			up = info;

		if (up != null)
			finish(map.father, up);
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
		today.add(Calendar.DATE, 1);

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

	@Override
	public TreeMap<String, Calendar> getBrothers(Calendar cal) {
		TreeMap<String, Calendar> ans = new TreeMap<String, Calendar>();
		Calendar d1 = (Calendar) cal.clone();
		Calendar d2 = (Calendar) cal.clone();
		firstDayofWeek(d1);
		lastDayofWeek(d2);
		d2.add(Calendar.DATE, 1);
		do {
			ans.put(this.getItemByCal(d1), d1);
			d1 = (Calendar) d1.clone();
			d1.add(Calendar.DATE, 1);
		} while (d1.before(d2));
		return ans;
	}

	/**
	 * ���ڼ����ַ�������
	 */
	public static final String[] WEEKDAY = { "����", "��", "һ", "��", "��", "��",
			"��", "��" };

	@Override
	public String getItemByCal(Calendar cal) {
		return String.format("%02d", cal.get(Calendar.DATE)) + "��" + " " + "��"
				+ WEEKDAY[cal.get(Calendar.DAY_OF_WEEK)];
	}

	@Override
	public String getPanelBorder(Calendar cal) {
		return cal.get(Calendar.YEAR) + " �� " + (cal.get(Calendar.MONTH) + 1)
				+ " �� " + this.getItemByCal(cal);
	}
}
