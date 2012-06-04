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
		final int size = today.tasks.getSize(showFinished);
		data = new Object[size][columnNames.length];

		Iterator<Entry<String, DayTask>> it = today.tasks.iterator();
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
	}

	/**
	 * ���һ�����񵽱�����һ��
	 * 
	 * @param task
	 */
	public void addTask(DayTask task) {
		final int old = data.length;
		final int cols = columnNames.length;
		Object[][] newdata = new Object[old + 1][cols];
		int i;
		// ֱ�Ӹ��ƾɵĶ�ά����
		for (i = 0; i < old; ++i)
			newdata[i] = data[i];
		// ���µ�һ������µ�����
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
		// �ڵ�1,2�в��ܱ༭
		if (col == 1 || col == 2)
			return false;

		// û�������ʱ��,����δ��������ǿ��Ե�
		if (!today.isWorking())
			if (!today.tasks.get((String) data[row][1]).finished)
				if (col == 0)
					return true;
				else
					// ����ǵ�3��
					return false;
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
		Date now = new Date(); // ��Ӧһ������¼�,�϶���Ҫ��ǰʱ��

		// ��3����������ɵı�־,��ֻ�������������е�����(��������������) �����ǵ�ǰ���������
		if (columnIndex == 3) {
			// ��ǰ�������,ͳ��ʱ��,��������,д�����
			task = today.tasks.get(today.cur);
			today.cur = null; // ����û����
			task.add(now.getTime() - today.begin.getTime());
			task.finished();
			today.tasks.writeTasks();

			// �������������
			today.startLazy = now;

			// �������ѡ����ѡ��״̬
			data[rowIndex][0] = false;
			data[rowIndex][2] = HMS(task.lastTime);
		}

		/*
		 * �������ǵ�0��,����aValue��״̬�ɿ����ǿ�ʼһ����������ͣһ������
		 */
		else if ((boolean) aValue) {
			// ����һ������
			today.cur = (String) data[rowIndex][1];
			today.begin = now;
			today.used = today.tasks.get(today.cur).lastTime;

			// ������������,��ǰһ�ο��е�ʱ����뵽����ʱ����
			today.vacancy += now.getTime() - today.startLazy.getTime();
		}
		// ��ͣһ������
		else {
			task = today.tasks.get(data[rowIndex][1]);
			today.cur = null;
			task.add(now.getTime() - today.begin.getTime());
			today.tasks.writeTasks();

			// �����ڿ�ʼ�ֿ�����
			today.startLazy = now;
		}

		// ��ǩ������Ϊ�˴�table���޸Ķ��仯��С
		updater.updateTaskShow();
	}
}
