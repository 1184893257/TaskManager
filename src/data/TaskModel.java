package data;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import data.task.DayTask;

/**
 * ��������ģ��<br>
 * �����������ʾ
 * 
 * @author lqy
 * 
 */
public class TaskModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * ����
	 */
	String[] columnNames = { "״̬", "��������", "Ԥ��ʱ��", "�������" };
	/**
	 * �������
	 */
	Object[][] data;
	/**
	 * ����ʱ����
	 */
	protected Today today;

	/**
	 * �������ģ�͵Ĺ��캯��
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
		// �ڵ�1,2�в��ܱ༭
		if (col == 1 || col == 2)
			return false;

		// û�������ʱ��,����δ��������ǿ��Ե�
		if (!today.isWorking()
				&& !today.tasks.get((String) data[row][1]).finished)
			if (col == 0)
				return true;
			else
				return false;

		/*
		 * �������ʱ��,���������еļ��������ǿ��Ե�, ��Ϊ��һ�׶��Ѿ��ų��˵�1,2�е����,��������϶��ǵ�0��3��
		 */
		else if (today.cur.equals(data[row][1]))
			return true;
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = aValue;

		DayTask task; // ��ǰ����
		/*
		 * ��3����������ɵı�־,��ֻ�������������е�����(��������������) �����ǵ�ǰ���������
		 */
		if (rowIndex == 3) {
			// ��ǰ�������,ͳ��ʱ��,��������,д�����
			task = today.tasks.get((String) data[rowIndex][1]);
			today.cur = null; // ����û����
			task.add(new Date().getTime() - today.begin.getTime());
			task.finished();
			today.tasks.writeTasks();

			// TODO ���±��,����û��,ֻ�Ǹ��ĸ���״̬

			// XXX ��ʾ��Ϣ
		}

		/*
		 * �������ǵ�0��,����aValue��״̬�ɿ����ǿ�ʼһ����������ͣһ������
		 */
		else if ((boolean) aValue) {
			// ����һ������
			today.cur = (String) data[rowIndex][1];

			// TODO ���±��,����û��,ֻ�Ǹ��ĸ���״̬
		}
		// ��ͣһ������
		else {
			task = today.tasks.get((String) data[rowIndex][1]);
			today.cur = null;
			task.add(new Date().getTime() - today.begin.getTime());
			today.tasks.writeTasks();

			// TODO ������ʾ
		}
	}
}
