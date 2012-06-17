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
		final int size = today.day.getSize(showFinished);
		data = new Object[size][columnNames.length];

		Iterator<Entry<String, DayTask>> it = today.day.iterator();
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
		this.fireTableDataChanged();
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

		// 激活一个任务或暂停一个任务
		if (col == 0)
			if (!today.day.get(data[row][1]).finished)
				return true;
			else
				return false;

		// 完成只限于正在执行的任务
		else if (today.isWorking() && today.cur.equals(data[row][1]))
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
			data[rowIndex][2] = HMS(today.finishCur(now));
		}

		// 如果点的是第0列,根据aValue的状态可看出是开始一个任务还是暂停一个任务
		else if ((boolean) aValue) {// 开启一个任务
			// 如果有别的任务正在执行,暂停它
			if (today.isWorking()) {
				int i;
				for (i = 0; i < data.length; ++i)
					if ((boolean) data[i][0]) {
						data[i][0] = false;
						data[i][2] = HMS(today.stopTask(now));
						break;
					}
				this.fireTableRowsUpdated(i, i);
			}

			// 激活新任务
			today.startTask((String) data[rowIndex][1], now);
		} else
			// 暂停一个任务
			data[rowIndex][2] = HMS(today.stopTask(now));

		data[rowIndex][columnIndex] = aValue;

		// 标签可能因为此次table的修改而变化大小
		updater.updateTaskShow();
	}
}
