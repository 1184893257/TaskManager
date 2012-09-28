package data;

import java.util.Comparator;

import data.task.Task;

/**
 * ��װ���ֵ�����������ıȽ�������
 * 
 * @author lqy
 * 
 */
public class Cmps {
	/**
	 * ������������
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
	 * ������ʱ������
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
	 * ������ʱ������
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

	// ����
	protected static final CmpByName cmp00 = new CmpByName();
	protected static final CmpByNeedTime cmp01 = new CmpByNeedTime();
	protected static final CmpByLastTime cmp02 = new CmpByLastTime();

	// ����
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
	 * �Ƚ���,�ֱ���:<br>
	 * ����������,����ʱ������,����ʱ������<br>
	 * ����������,����ʱ������,����ʱ������
	 */
	public static final Object[][] cmps = new Object[][] {
			{ cmp00, cmp01, cmp02 }, { cmp10, cmp11, cmp12 } };
}
