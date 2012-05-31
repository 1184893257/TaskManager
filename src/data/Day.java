package data;

import java.util.*;

import data.task.DayTask;

/**
 * 此类包含一天的任务<br>
 * 有多个DayTask
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
