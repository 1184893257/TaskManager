package data;

import java.util.Calendar;

import data.tasks.*;

/**
 * 此类是Today类的子类<br>
 * <p>
 * Today类的年月周日任务集合在程序开始运行时以今日日期为证据读入, 之后不再变更,而编辑窗体需要编辑将来的任务,这个时间可能不在本日本周本月本年内,
 * 因此需要重新设置年月周日任务集合
 * </p>
 * 
 * @author lqy
 * 
 */
public class ADay extends Today {
	public void setYear(Calendar cal) {
		year = Year.newYear(cal, false);
	}

	public void setMonth(Calendar cal) {
		month = Month.newMonth(cal, year, false);
	}

	public void setWeek(Calendar cal) {
		week = Week.newWeek(cal, month, false);
	}

	public void setDay(Calendar cal) {
		day = Day.newDay(cal, week, false);
	}
}
