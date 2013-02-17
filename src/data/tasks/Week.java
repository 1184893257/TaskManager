package data.tasks;

import java.io.File;
import java.util.*;

import data.task.*;

public class Week extends TaskMap<WeekTask, MonthTask> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * ���ݱ��ܵĵ�һ������ڹ����������Ĵ��·��,�����Զ������񼯺�
	 * 
	 * @param year
	 *            ��
	 * @param month
	 *            ��
	 * @param day
	 *            ��
	 */
	protected Week(int year, int month, int day) {
		dir = DATA + File.separator + year + File.separator + month;
		path = dir + File.separator + "w" + day;
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, WeekTask>();
	}

	/**
	 * <p>
	 * ��cal�޸�Ϊcal�����ܵĵ�һ��
	 * </p>
	 * ������ܵ���һ��cal����һ������,��ôcal�����޸�Ϊ���ܵ�����һ, ����cal���޸�Ϊ����µ�1��
	 * 
	 * @param cal
	 *            Ҫ�޸ĵ�����,���������ܻ�Ķ�
	 */
	public static void firstDayofWeek(Calendar cal) {
		Calendar cal2 = (Calendar) cal.clone();

		int days;// ��days��ɵ�������ܵ���һ
		days = cal.get(Calendar.DAY_OF_WEEK);
		days = days == Calendar.SUNDAY ? 6 : days - 2;

		cal2.add(Calendar.DATE, -days);
		if (cal2.get(Calendar.MONTH) == cal.get(Calendar.MONTH))// ��һ���������
			cal.add(Calendar.DATE, -days);
		else
			cal.set(Calendar.DATE, 1);
	}

	/**
	 * ��cal�޸�Ϊ�����ܵ����һ��
	 * 
	 * @param cal
	 */
	public static void lastDayofWeek(Calendar cal) {
		Calendar cal2 = (Calendar) cal.clone();

		int days;// ��days������ܵ�������
		days = cal.get(Calendar.DAY_OF_WEEK);
		days = days == Calendar.SUNDAY ? 0 : 8 - days;

		cal2.add(Calendar.DATE, days);
		if (cal2.get(Calendar.MONTH) == cal.get(Calendar.MONTH))// �������������
			cal.add(Calendar.DATE, days);
		else {
			cal.set(Calendar.DATE, 1);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
		}
	}

	/**
	 * ��������cal���������񼯺�
	 * 
	 * @param cal
	 *            ����<b>������һ�ܵĵ�һ��</b>
	 * @param bringLastWeek
	 *            Ҫת����һ��δ��ɵ�����������?
	 */
	protected Week(Calendar cal, boolean bringLastWeek) {
		this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH), cal
				.get(Calendar.DATE));
		if (bringLastWeek) {
			// �����ϸ��ܵ������񼯺�
			cal.add(Calendar.DATE, -1);
			firstDayofWeek(cal);
			Week lastWeek = new Week(cal, false);
			this.bringLast(lastWeek);
		}
	}

	/**
	 * �ṩ���ⲿʹ�õ������񼯺ϵĹ��췽��
	 * 
	 * @param cal
	 *            ����
	 * @param father
	 *            �����񼯺�
	 * @return cal�����ܵ������񼯺�
	 */
	public static Week newWeek(Calendar cal, Month father) {
		return newWeek(cal, father, true);
	}

	public static Week newWeek(Calendar cal, Month father, boolean bringLastWeek) {
		Calendar cal2 = (Calendar) cal.clone();
		firstDayofWeek(cal2);
		Week ans = new Week(cal2, bringLastWeek);
		ans.father = father;
		return ans;
	}

	@Override
	public TreeMap<String, Calendar> getBrothers(Calendar cal) {
		TreeMap<String, Calendar> ans = new TreeMap<String, Calendar>();

		Calendar d = (Calendar) cal.clone();
		d.set(Calendar.DATE, 1);

		do {
			Calendar d2 = d;
			ans.put(this.getItemByCal(d2), d2);
			d = (Calendar) d2.clone();
			lastDayofWeek(d);
			d.add(Calendar.DATE, 1);
		} while (d.get(Calendar.MONTH) == cal.get(Calendar.MONTH));
		return ans;
	}

	/**
	 * ��cal�������µĵڼ���
	 * 
	 * @param cal
	 * @return
	 */
	protected int getWeekofMonth(Calendar cal) {
		// ���µ�һ�������ڼ�?
		Calendar cal2 = (Calendar) cal.clone();
		cal2.set(Calendar.DATE, 1);
		int ans = cal2.get(Calendar.DAY_OF_WEEK);

		ans = ans == Calendar.SUNDAY ? 6 : ans - 2;
		ans += cal.get(Calendar.DATE) - 1;
		ans = ans / 7 + 1;
		return ans;
	}

	@Override
	public String getItemByCal(Calendar cal) {
		Calendar d2 = (Calendar) cal.clone();
		Calendar d3 = (Calendar) cal.clone();
		firstDayofWeek(d2);
		lastDayofWeek(d3);
		return "��" + getWeekofMonth(cal) + "�� " + d2.get(Calendar.DATE) + "-"
				+ d3.get(Calendar.DATE);
	}

	@Override
	public String getPanelBorder(Calendar cal) {
		return cal.get(Calendar.YEAR) + " �� " + (cal.get(Calendar.MONTH) + 1)
				+ " �� " + this.getItemByCal(cal);
	}

	@Override
	public Calendar firstDay(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		firstDayofWeek(c);
		return c;
	}

	@Override
	public Calendar lastDay(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		lastDayofWeek(c);
		return c;
	}

}
