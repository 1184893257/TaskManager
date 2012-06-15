package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

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

			// ѭ��ת������δ��ɵ�����
			Iterator<Entry<String, MonthTask>> it = lastMonth.iterator();
			MonthTask d;
			while (it.hasNext()) {
				d = it.next().getValue();
				if (!d.finished) {
					tasks.put(d.info, d);
					it.remove();
				}
			}
			this.writeTasks();// �����ܵ��޸�д��
			lastMonth.writeTasks();// �����ܵ��޸�д��
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
		Month ans = new Month(cal, true);
		ans.father = father;
		return ans;
	}

}
