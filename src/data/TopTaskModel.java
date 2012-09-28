package data;

import static gui.StaticMethod.HMS;

import java.util.Comparator;
import java.util.Iterator;
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
	/**
	 * ������һ��"�ܼ�"<br>
	 * ���ַ������������ݱ�����,���񲻵��д�����
	 */
	public static final String TOTAL = "�ܼ�";

	protected String[] colNames;
	protected Object[][] data;
	/**
	 * ������ĳ������׶ε����񼯺�,��Today�Ķ������ADay�Ķ���
	 */
	protected Today aday;
	/**
	 * ���ڴ˱������ıȽ���
	 */
	protected Comparator<Task> cmp;

	/**
	 * �������õĹ��췽��
	 */
	protected TopTaskModel() {
	}

	/**
	 * �������ģ�͵Ĺ���
	 * 
	 * @param aday
	 *            ������ĳ������׶ε����񼯺�
	 */
	public TopTaskModel(Today aday) {
		this.aday = aday;
		colNames = new String[] { "������", "����ʱ��", "����ʱ��", "������" };
		showTasks(true);
	}

	/**
	 * ���ñȽ����Ըı�����ʽ
	 * 
	 * @param cmp
	 *            �Ƚ���
	 */
	public void setCmp(Comparator<Task> cmp) {
		this.cmp = cmp;
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

		Iterator<E> it = tasks.iterator(cmp);
		int i = 0;
		E task;
		long totalNeed = 0, totalUsed = 0;// ����ʱ��,����ʱ��
		while (it.hasNext()) {
			task = it.next();
			totalNeed += task.needTime;
			totalUsed += task.lastTime;
			if (showFinished || !task.finished)
				data[i++] = new Object[] { task.info, HMS(task.needTime),
						HMS(task.lastTime),
						task.father == null ? "NULL" : task.father };
		}
		data[size] = new Object[] { TopTaskModel.TOTAL, HMS(totalNeed),
				HMS(totalUsed), "" };
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

	/**
	 * ��õ�ǰ����ִ�е�����<br>
	 * ���������������Ϊ�������ģ������Today�Ķ���,<br>
	 * ��������޸ĺ�ɾ����ʱ����Ҫ�õ�Today�����cur��Ա,���Ծͽ��model�����
	 * 
	 * @return ��ǰ����ִ�е�����
	 */
	public String getCur() {
		return aday.cur;
	}

	/**
	 * ���赱ǰ����ִ�е�����,ֻ�н��������������õ��������
	 * 
	 * @param newCur
	 *            �µĵ�ǰ����
	 */
	public void setCur(String newCur) {
		aday.cur = newCur;
	}

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
