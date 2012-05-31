package data;

import java.util.*;

import data.task.DayTask;

/**
 * �������һ�������<br>
 * �ж��DayTask
 * 
 * @author lqy
 * 
 */
public class Day extends TaskMap<DayTask> {

	public Day(int year, int month, int day) {
	}

	public Day() {
		path="today";
		if(!this.readTasks()){
			tasks = new TreeMap<String, DayTask>();
		}
	}
}
