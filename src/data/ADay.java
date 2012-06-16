package data;

import java.util.Calendar;

import data.tasks.*;

/**
 * ������Today�������<br>
 * <p>
 * Today��������������񼯺��ڳ���ʼ����ʱ�Խ�������Ϊ֤�ݶ���, ֮���ٱ��,���༭������Ҫ�༭����������,���ʱ����ܲ��ڱ��ձ��ܱ��±�����,
 * �����Ҫ�������������������񼯺�
 * </p>
 * 
 * @author lqy
 * 
 */
public class ADay extends Today {
	public void setYear(Calendar cal) {
		year = Year.newYear(cal);
	}

	public void setMonth(Calendar cal) {
		month = Month.newMonth(cal, year);
	}

	public void setWeek(Calendar cal) {
		week = Week.newWeek(cal, month);
	}

	public void setDay(Calendar cal) {
		day = Day.newDay(cal, week);
	}
}