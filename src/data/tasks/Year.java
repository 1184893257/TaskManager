package data.tasks;

import java.io.File;
import java.util.*;

import data.task.*;

public class Year extends TaskMap<YearTask, Task> {
	@Override
	protected void buildDir(String dir) {
		this.dir = dir;
	}

	/**
	 * ʹ����ݹ��������񼯺�
	 * 
	 * @param year
	 *            ��
	 */
	protected Year(int year) {
		dir = DATA + File.separator + year;
		path = dir + File.separator + "year";
		this.buildDir(dir);
		if (!this.readTasks())
			tasks = new TreeMap<String, YearTask>();
	}

	/**
	 * ʹ�����ڹ��������񼯺�
	 * 
	 * @param cal
	 *            ����
	 * @param bringLastYear
	 *            �Ƿ�ת��ȥ��δ��ɵ������񵽽���
	 */
	protected Year(Calendar cal, boolean bringLastYear) {
		this(cal.get(Calendar.YEAR));
		if (bringLastYear) {
			// ����ȥ��������񼯺�
			Calendar cal2 = (Calendar) cal.clone();
			cal2.add(Calendar.YEAR, -1);
			Year lastYear = new Year(cal2, false);
			this.bringLast(lastYear);
		}
	}

	/**
	 * ���ⲿ�ṩ�ĸ������������깹�������񼯺ϵľ�̬����
	 * 
	 * @param cal
	 *            ����
	 * @return cal������������񼯺�
	 */
	public static Year newYear(Calendar cal) {
		return newYear(cal, true);
	}

	public static Year newYear(Calendar cal, boolean bringLastYear) {
		return new Year(cal, bringLastYear);
	}

	@Override
	public TreeMap<String, Calendar> getBrothers(Calendar cal) {
		TreeMap<String, Calendar> ans = new TreeMap<String, Calendar>();
		File[] folders = new File(DATA).listFiles();

		// ��data�ļ����������ѽ�������ݼ��뵽ans��
		for (File e : folders) {
			String name = e.getName();
			if (e.isDirectory() && name.matches("\\d+")) {
				Calendar cal2 = (Calendar) cal.clone();
				cal2.set(Integer.parseInt(name), 0, 1);
				ans.put(this.getItemByCal(cal2), cal2);
			}
		}

		// �����һ����һ�ŵ����
		int biggest = ans.lastEntry().getValue().get(Calendar.YEAR);
		Calendar cal3 = (Calendar) cal.clone();
		cal3.set(biggest + 1, 0, 1);
		ans.put(this.getItemByCal(cal3), cal3);
		return ans;
	}

	@Override
	public String getItemByCal(Calendar cal) {
		return cal.get(Calendar.YEAR) + " ��";
	}

	@Override
	public String getPanelBorder(Calendar cal) {
		return this.getItemByCal(cal);
	}
}
