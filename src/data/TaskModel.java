package data;

import inter.UpdateTable;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

import data.task.DayTask;

import static gui.FormatTime.*;

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
	 * ���±���ǩ<br>
	 * �����ʱ��Ҳ���±�ǩ����ʾ
	 */
	protected UpdateTable updater;

	/**
	 * �������ģ�͵Ĺ��캯��
	 */
	public TaskModel(Today today, UpdateTable updater) {
		this.updater = updater;
		this.today = today;
		showTasks(true);// ��ʼ״̬����ʾ����ɵ�
	}

	/**
	 * ������data�Ը�����ʾ
	 * 
	 * @param showFinished
	 *            ��ʾ����ɵ�����?
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
						// ��ǰ����Ϊtrue
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
		// �ڵ�1,2�в��ܱ༭
		if (col == 1 || col == 2)
			return false;

		// ����һ���������ͣһ������
		if (col == 0)
			if (!today.day.get(data[row][1]).finished)
				return true;
			else
				return false;

		// ���ֻ��������ִ�е�����
		else if (today.isWorking() && today.cur.equals(data[row][1]))
			return true;
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Date now = new Date(); // ��Ӧһ������¼�,�϶���Ҫ��ǰʱ��

		// ��3����������ɵı�־,��ֻ�������������е�����(��������������) �����ǵ�ǰ���������
		if (columnIndex == 3) {
			// �������ѡ����ѡ��״̬
			data[rowIndex][0] = false;
			data[rowIndex][2] = HMS(today.finishCur(now));
		}

		// �������ǵ�0��,����aValue��״̬�ɿ����ǿ�ʼһ����������ͣһ������
		else if ((boolean) aValue) {// ����һ������
			// ����б����������ִ��,��ͣ��
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

			// ����������
			today.startTask((String) data[rowIndex][1], now);
		} else
			// ��ͣһ������
			data[rowIndex][2] = HMS(today.stopTask(now));

		data[rowIndex][columnIndex] = aValue;

		// ��ǩ������Ϊ�˴�table���޸Ķ��仯��С
		updater.updateTaskShow();
	}
}
