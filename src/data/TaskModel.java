package data;

import inter.UpdateTable;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

import data.task.DayTask;

import static gui.FormatTime.*;

/**
 * 表格的数据模型<br>
 * 负责任务的显示
 * 
 * @author lqy
 * 
 */
public class TaskModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * 列名
	 */
	String[] columnNames = { "状态", "任务内容", "预计时间", "完成任务" };
	/**
	 * 表格数据
	 */
	Object[][] data;
	/**
	 * 运行时数据
	 */
	protected Today today;
	/**
	 * 更新表格标签<br>
	 * 点击的时候也更新标签的显示
	 */
	protected UpdateTable updater;

	/**
	 * 表格数据模型的构造函数
	 */
	public TaskModel(Today today, UpdateTable updater) {
		this.updater = updater;
		this.today = today;
		showTasks(true);// 初始状态是显示已完成的
	}

	/**
	 * 重新设data以更新显示
	 * 
	 * @param showFinished
	 *            显示已完成的任务?
	 */
	public void showTasks(boolean showFinished) {
		final int size = today.tasks.getSize(showFinished);
		data = new Object[size][columnNames.length];

		Iterator<Entry<String, DayTask>> it = today.tasks.iterator();
		int i = 0;
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			if (showFinished || !task.finished)
				data[i++] = new Object[] {
						// 当前任务为true
						null == today.cur ? false : today.cur.equals(task.info),
						task.info,
						task.finished ? HMS(task.lastTime) : HMS(task.needTime),
						task.finished };
		}
	}

	/**
	 * 添加一个任务到表格最后一行
	 * 
	 * @param task
	 */
	public void addTask(DayTask task) {
		final int old = data.length;
		final int cols = columnNames.length;
		Object[][] newdata = new Object[old + 1][cols];
		int i;
		// 直接复制旧的二维数组
		for (i = 0; i < old; ++i)
			newdata[i] = data[i];
		// 在新的一行添加新的任务
		newdata[i] = new Object[] { false, task.info, HMS(task.needTime), false };
		data = newdata;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		// 在第1,2列不能编辑
		if (col == 1 || col == 2)
			return false;

		// 没有任务的时候,激活未完成任务是可以的
		if (!today.isWorking())
			if (!today.tasks.get((String) data[row][1]).finished)
				if (col == 0)
					return true;
				else
					// 点的是第3列
					return false;
			else
				return false;

		/*
		 * 有任务的时候,任务所在行的激活和完成是可以的, 因为第一阶段已经排除了第1,2列的情况,所以这里肯定是第0或3列
		 */
		else if (today.cur.equals(data[row][1]))
			return true;
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = aValue;

		DayTask task; // 当前任务
		Date now = new Date(); // 响应一个点击事件,肯定需要当前时间

		// 第3列是任务完成的标志,且只可能是正在运行的任务(别的这个键按不了) 所以是当前任务完成了
		if (columnIndex == 3) {
			// 当前任务完成,统计时间,结束任务,写入更改
			task = today.tasks.get(today.cur);
			today.cur = null; // 现在没任务
			task.add(now.getTime() - today.begin.getTime());
			task.finished();
			today.tasks.writeTasks();

			// 从现在起空闲了
			today.startLazy = now;

			// 清除激活选择框的选中状态
			data[rowIndex][0] = false;
			data[rowIndex][2] = HMS(task.lastTime);
		}

		/*
		 * 如果点的是第0列,根据aValue的状态可看出是开始一个任务还是暂停一个任务
		 */
		else if ((boolean) aValue) {
			// 开启一个任务
			today.cur = (String) data[rowIndex][1];
			today.begin = now;
			today.used = today.tasks.get(today.cur).lastTime;

			// 从现在起工作了,把前一段空闲的时间加入到空闲时间中
			today.vacancy += now.getTime() - today.startLazy.getTime();
		}
		// 暂停一个任务
		else {
			task = today.tasks.get(data[rowIndex][1]);
			today.cur = null;
			task.add(now.getTime() - today.begin.getTime());
			today.tasks.writeTasks();

			// 从现在开始又空闲了
			today.startLazy = now;
		}

		// 标签可能因为此次table的修改而变化大小
		updater.updateTaskShow();
	}
}
