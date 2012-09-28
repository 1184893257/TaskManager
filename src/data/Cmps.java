package data;

import java.util.Comparator;

import data.task.Task;

/**
 * 封装各种单个任务排序的比较器的类
 * 
 * @author lqy
 * 
 */
public class Cmps {
	/**
	 * 按任务名排序
	 * 
	 * @author lqy
	 * 
	 */
	protected static class CmpByName implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			return o1.info.compareTo(o2.info);
		}

	}

	/**
	 * 按所需时间排序
	 * 
	 * @author lqy
	 * 
	 */
	protected static class CmpByNeedTime implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			return Long.compare(o1.needTime, o2.needTime);
		}

	}

	/**
	 * 按已用时间排序
	 * 
	 * @author lqy
	 * 
	 */
	protected static class CmpByLastTime implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			return Long.compare(o1.lastTime, o2.lastTime);
		}

	}

	// 正序
	protected static final CmpByName cmp00 = new CmpByName();
	protected static final CmpByNeedTime cmp01 = new CmpByNeedTime();
	protected static final CmpByLastTime cmp02 = new CmpByLastTime();

	// 逆序
	protected static final CmpByName cmp10 = new CmpByName() {

		@Override
		public int compare(Task o1, Task o2) {
			return -super.compare(o1, o2);
		}

	};
	protected static final CmpByNeedTime cmp11 = new CmpByNeedTime() {

		@Override
		public int compare(Task o1, Task o2) {
			return -super.compare(o1, o2);
		}

	};
	protected static final CmpByLastTime cmp12 = new CmpByLastTime() {

		@Override
		public int compare(Task o1, Task o2) {
			return -super.compare(o1, o2);
		}

	};

	/**
	 * 比较器,分别是:<br>
	 * 任务名正序,所需时间正序,所用时间正序<br>
	 * 任务名逆序,所需时间逆序,所用时间逆序
	 */
	public static final Object[][] cmps = new Object[][] {
			{ cmp00, cmp01, cmp02 }, { cmp10, cmp11, cmp12 } };
}
