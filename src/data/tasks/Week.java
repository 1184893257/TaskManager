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

}
