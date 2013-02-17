package data.tasks;

import java.io.File;
import java.util.*;

import data.task.*;

public class Month extends TaskMap<MonthTask, YearTask> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * ʹ�����¹��������񼯺ϵĴ洢·��
	 * 
	 * @param year
	 *            ��
	 * @param month
	 *            ��
	 */
	protected Month(int year, int month) {
		dir = DATA + File.separator + year + File.separator + month;
		path = dir + File.separator + "month";
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, MonthTask>();
	}

	/**
	 * �������ڹ��������񼯺�
	 * 
	 * @param cal
	 *            ����
	 * @param bringLastMonth
	 *            �Ƿ�ת�����µ����������?
	 */
	protected Month(Calendar cal, boolean bringLastMonth) {
		this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH));
		if (bringLastMonth) {
			// �����ϸ��µ������񼯺�
			Calendar cal2 = (Calendar) cal.clone();
			cal2.add(Calendar.MONTH, -1);
			Month lastMonth = new Month(cal2, false);
			this.bringLast(lastMonth);
		}
	}

	/**
	 * �ṩ���ⲿʹ�õ����������������񼯺ϵľ�̬����
	 * 
	 * @param cal
	 *            ����
	 * @param father
	 *            �����񼯺�
	 * @return cal�����µ������񼯺�
	 */
	public static Month newMonth(Calendar cal, Year father) {
		return newMonth(cal, father, true);
	}

	public static Month newMonth(Calendar cal, Year father,
			boolean bringLastWeek) {
		Month ans = new Month(cal, bringLastWeek);
		ans.father = father;
		return ans;
	}

	@Override
	public TreeMap<String, Calendar> getBrothers(Calendar cal) {
		TreeMap<String, Calendar> ans = new TreeMap<String, Calendar>();
		int year = cal.get(Calendar.YEAR);
		for (int month = 0; month < 12; ++month) {
			Calendar cal2 = (Calendar) cal.clone();
			cal2.set(year, month, 1);
			ans.put(this.getItemByCal(cal2), cal2);
		}
		return ans;
	}

	@Override
	public String getItemByCal(Calendar cal) {
		return String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + " ��";
	}

	@Override
	public String getPanelBorder(Calendar cal) {
		return cal.get(Calendar.YEAR) + " �� " + this.getItemByCal(cal);
	}

	@Override
	public Calendar firstDay(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		c.set(Calendar.DATE, 1);
		return c;
	}

	@Override
	public Calendar lastDay(Calendar cal) {
		Calendar c = (Calendar) cal.clone();
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c;
	}

}
