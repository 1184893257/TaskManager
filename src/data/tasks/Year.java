package data.tasks;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

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

			// ѭ��ת��ȥ��δ��ɵ�����
			Iterator<Entry<String, YearTask>> it = lastYear.iterator();
			YearTask d;
			while (it.hasNext()) {
				d = it.next().getValue();
				if (!d.finished) {
					tasks.put(d.info, d);
					it.remove();
				}
			}
			this.writeTasks();// �����ܵ��޸�д��
			lastYear.writeTasks();// �����ܵ��޸�д��
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
		return new Year(cal, true);
	}
}