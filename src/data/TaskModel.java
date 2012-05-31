package data;

import javax.swing.table.AbstractTableModel;

/**
 * 表格的数据模型<br>
 * 负责任务的显示
 * 
 * @author lqy
 * 
 */
public class TaskModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	String[] columnNames = { "状态", "任务内容", "预计时间", "完成任务" };
	Object[][] data;

	/**
	 * 表格数据模型的构造函数
	 */
	public TaskModel() {
		data = new Object[][] { { null, "A", "1:00:00", null },
				{ null, "B", "1:30:00", null } };
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		if (col == 0)
			return true;
		return false;
	}
}
