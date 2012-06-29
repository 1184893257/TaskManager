package data;

import static gui.FormatTime.HMS;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

import data.task.Task;
import data.tasks.TaskMap;

/**
 * 任务表格的通用数据模型(抽象的)
 * 
 * @author lqy
 * 
 * @param <E>
 *            此模型的任务集合中每个任务的类型
 */
public abstract class TopTaskModel<E extends Task> extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	protected String[] colNames = { "任务名", "所需时间", "已用时间", "父任务" };
	protected Object[][] data;
	/**
	 * 包含了某天各个阶段的任务集合,是Today的对象或者ADay的对象
	 */
	protected Today aday;

	/**
	 * 表格数据模型的构造
	 * 
	 * @param aday
	 *            包含了某天各个阶段的任务集合
	 */
	public TopTaskModel(Today aday) {
		this.aday = aday;
		showTasks(true);
	}

	/**
	 * 刷新显示所有任务的信息
	 * 
	 * @param showFinished
	 *            是否显示已完成的任务
	 */
	public void showTasks(boolean showFinished) {
		TaskMap<E, ? extends Task> tasks = getTasks();
		final int size = tasks.getSize(showFinished);
		data = new Object[size + 1][colNames.length];

		Iterator<Entry<String, E>> it = tasks.iterator();
		int i = 0;
		E task;
		long totalNeed = 0, totalUsed = 0;// 总需时间,总用时间
		while (it.hasNext()) {
			task = it.next().getValue();
			totalNeed += task.needTime;
			totalUsed += task.lastTime;
			if (showFinished || !task.finished)
				data[i++] = new Object[] { task.info, HMS(task.needTime),
						HMS(task.lastTime),
						task.father == null ? "NULL" : task.father };
		}
		data[size] = new Object[] { "total", HMS(totalNeed), HMS(totalUsed), "" };
		this.fireTableDataChanged();
	}

	/**
	 * 获得对应表格的任务集合<br>
	 * 表格数据模型的子类必须实现的方法,不同的表格关注的任务集合不同,<br>
	 * 一般都是返回aday的某个成员
	 * 
	 * @return 此表格数据模型关注的的任务集合
	 */
	public abstract TaskMap<E, ? extends Task> getTasks();

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}

	@Override
	public int getColumnCount() {
		return colNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}
