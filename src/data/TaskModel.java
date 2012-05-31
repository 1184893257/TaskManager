package data;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import data.task.DayTask;

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
	 * 表格数据模型的构造函数
	 */
	public TaskModel(Today today) {
		this.today = today;
		data = new Object[][] { { false, "A", "1:00:00", false },
				{ false, "B", "1:30:00", false },
				{ false, "C", "2:30:00", false } };
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
		if (!today.isWorking()
				&& !today.tasks.get((String) data[row][1]).finished)
			if (col == 0)
				return true;
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
		/*
		 * 第3列是任务完成的标志,且只可能是正在运行的任务(别的这个键按不了) 所以是当前任务完成了
		 */
		if (rowIndex == 3) {
			// 当前任务完成,统计时间,结束任务,写入更改
			task = today.tasks.get((String) data[rowIndex][1]);
			today.cur = null; // 现在没任务
			task.add(new Date().getTime() - today.begin.getTime());
			task.finished();
			today.tasks.writeTasks();

			// TODO 更新表格,行列没变,只是更改各行状态

			// XXX 显示信息
		}

		/*
		 * 如果点的是第0列,根据aValue的状态可看出是开始一个任务还是暂停一个任务
		 */
		else if ((boolean) aValue) {
			// 开启一个任务
			today.cur = (String) data[rowIndex][1];

			// TODO 更新表格,行列没变,只是更改各行状态
		}
		// 暂停一个任务
		else {
			task = today.tasks.get((String) data[rowIndex][1]);
			today.cur = null;
			task.add(new Date().getTime() - today.begin.getTime());
			today.tasks.writeTasks();

			// TODO 更新显示
		}
	}
}
