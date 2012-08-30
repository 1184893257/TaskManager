package data;

import static gui.StaticMethod.HMS;

import java.util.*;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import gui.TopTaskTable;
import inter.Updater;
import data.task.*;
import data.tasks.TaskMap;

public class TodayModel extends TopTaskModel<DayTask> {
	private static final long serialVersionUID = 1L;

	protected Class<?>[] colClasses = { Boolean.class, String.class,
			String.class, Boolean.class };
	/**
	 * 顶层界面的刷新接口<br>
	 * 会在setValueAt中用到
	 */
	protected Updater updater;
	/**
	 * 周任务的表格<br>
	 * 用于在setValueAt后更新父表格
	 */
	protected TopTaskTable<WeekTask> father;

	/**
	 * 构造今天的表格模型
	 * 
	 * @param today
	 *            Today的对象
	 * @param updater
	 *            顶层界面的刷新接口
	 * @param father
	 *            周任务的表格
	 */
	public TodayModel(Today today, Updater updater,
			TopTaskTable<WeekTask> father) {
		colNames = new String[] { "状态", "任务内容", "预计时间", "完成任务" };
		this.aday = today;
		this.updater = updater;
		this.father = father;
		showTasks(true);
	}

	@Override
	public void showTasks(boolean showFinished) {
		TaskMap<DayTask, ? extends Task> tasks = this.getTasks();

		final int size = tasks.getSize(showFinished);
		data = new Object[size][colNames.length];

		Iterator<Entry<String, DayTask>> it = tasks.iterator();
		int i = 0;
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			if (showFinished || !task.finished)
				data[i++] = new Object[] {
						// 当前任务为true
						null == aday.cur ? false : aday.cur.equals(task.info),
						task.info,
						task.finished ? HMS(task.lastTime) : HMS(task.needTime),
						task.finished };
		}
		this.fireTableDataChanged();
	}

	@Override
	public TaskMap<DayTask, ? extends Task> getTasks() {
		return aday.day;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return this.colClasses[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// 在第1,2列不能编辑
		if (columnIndex == 1 || columnIndex == 2)
			return false;

		// 激活一个任务或暂停一个任务
		if (columnIndex == 0)
			if (!aday.day.get(data[rowIndex][1]).finished)
				return true;
			else
				return false;

		// 完成只限于正在执行的任务
		else if (aday.isWorking() && aday.cur.equals(data[rowIndex][1]))
			return true;
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Date now = new Date(); // 响应一个点击事件,肯定需要当前时间

		// 第3列是任务完成的标志,且只可能是正在运行的任务(别的这个键按不了) 所以是当前任务完成了
		if (columnIndex == 3) {
			// 清除激活选择框的选中状态
			data[rowIndex][0] = false;
			data[rowIndex][2] = HMS(aday.finishCur(now));
		}

		// 如果点的是第0列,根据aValue的状态可看出是开始一个任务还是暂停一个任务
		else if ((Boolean) aValue) {// 开启一个任务
			// 如果有别的任务正在执行,暂停它
			if (aday.isWorking()) {
				int i;
				for (i = 0; i < data.length; ++i)
					if ((Boolean) data[i][0]) {
						data[i][0] = false;
						data[i][2] = HMS(aday.stopTask(now));
						break;
					}
				this.fireTableRowsUpdated(i, i);
			}

			// 激活新任务
			aday.startTask((String) data[rowIndex][1], now);
		} else
			// 暂停一个任务
			data[rowIndex][2] = HMS(aday.stopTask(now));

		data[rowIndex][columnIndex] = aValue;

		// 标签可能因为此次table的修改而变化大小
		father.updateFromMem();

		// 让更新界面的操作在setValueAt事件完成之后进行,
		// 这样就不会因为更新界面的时候重新调整表格大小时又执行未完成的setValueAt事件
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				updater.update();
			}

		});
	}

}
