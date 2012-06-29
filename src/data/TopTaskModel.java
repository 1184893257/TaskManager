package data;

import static gui.FormatTime.HMS;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

import data.task.Task;
import data.tasks.TaskMap;

/**
 * �������ͨ������ģ��(�����)
 * 
 * @author lqy
 * 
 * @param <E>
 *            ��ģ�͵����񼯺���ÿ�����������
 */
public abstract class TopTaskModel<E extends Task> extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	protected String[] colNames = { "������", "����ʱ��", "����ʱ��", "������" };
	protected Object[][] data;
	/**
	 * ������ĳ������׶ε����񼯺�,��Today�Ķ������ADay�Ķ���
	 */
	protected Today aday;

	/**
	 * �������ģ�͵Ĺ���
	 * 
	 * @param aday
	 *            ������ĳ������׶ε����񼯺�
	 */
	public TopTaskModel(Today aday) {
		this.aday = aday;
		showTasks(true);
	}

	/**
	 * ˢ����ʾ�����������Ϣ
	 * 
	 * @param showFinished
	 *            �Ƿ���ʾ����ɵ�����
	 */
	public void showTasks(boolean showFinished) {
		TaskMap<E, ? extends Task> tasks = getTasks();
		final int size = tasks.getSize(showFinished);
		data = new Object[size + 1][colNames.length];

		Iterator<Entry<String, E>> it = tasks.iterator();
		int i = 0;
		E task;
		long totalNeed = 0, totalUsed = 0;// ����ʱ��,����ʱ��
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
	 * ��ö�Ӧ�������񼯺�<br>
	 * �������ģ�͵��������ʵ�ֵķ���,��ͬ�ı���ע�����񼯺ϲ�ͬ,<br>
	 * һ�㶼�Ƿ���aday��ĳ����Ա
	 * 
	 * @return �˱������ģ�͹�ע�ĵ����񼯺�
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
