package data;

import static gui.StaticMethod.HMS;

import java.awt.Window;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import gui.FinishDialog;
import gui.TopTaskTable;
import inter.Updater;
import data.task.*;
import data.tasks.TaskMap;

public class TodayModel extends TopTaskModel<DayTask> {
	private static final long serialVersionUID = 1L;

	protected Class<?>[] colClasses = { Boolean.class, String.class,
			String.class, Boolean.class };
	/**
	 * ��������ˢ�½ӿ�<br>
	 * ����setValueAt���õ�
	 */
	protected Updater updater;
	/**
	 * ������ı��<br>
	 * ������setValueAt����¸����
	 */
	protected TopTaskTable<WeekTask> father;
	/**
	 * ���ṩ����ʱ��ĶԻ���
	 */
	protected FinishDialog finisher;

	/**
	 * �������ı��ģ��
	 * 
	 * @param owner
	 *            ��finisher��owner
	 * @param today
	 *            Today�Ķ���
	 * @param updater
	 *            ��������ˢ�½ӿ�
	 * @param father
	 *            ������ı��
	 */
	public TodayModel(Window owner, Today today, Updater updater,
			TopTaskTable<WeekTask> father) {
		finisher = new FinishDialog(owner);
		colNames = new String[] { "״̬", "��������", "Ԥ��ʱ��", "�������" };
		this.aday = today;
		this.updater = updater;
		this.father = father;
		showTasks(true);
	}

	@Override
	public void showTasks(boolean showFinished) {
		TaskMap<DayTask, ? extends Task> tasks = this.getTasks();

		final int size = tasks.getSize(showFinished);
		data = new Object[size][colNames.length];

		Iterator<Entry<String, DayTask>> it = tasks.iterator();
		int i = 0;
		DayTask task;
		while (it.hasNext()) {
			task = it.next().getValue();
			if (showFinished || !task.finished)
				data[i++] = new Object[] {
						// ��ǰ����Ϊtrue
						null == aday.cur ? false : aday.cur.equals(task.info),
						task.info,
						task.finished ? HMS(task.lastTime) : HMS(task.needTime),
						task.finished };
		}
		this.fireTableDataChanged();
	}

	@Override
	public TaskMap<DayTask, ? extends Task> getTasks() {
		return aday.day;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return this.colClasses[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// �ڵ�1,2�в��ܱ༭
		if (columnIndex == 1 || columnIndex == 2)
			return false;

		// ����ɵ������ܽ��� ����\�ж�\��� ����
		if (aday.day.get(data[rowIndex][1]).finished)
			return false;
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Date now = new Date(); // ��Ӧһ������¼�,�϶���Ҫ��ǰʱ��

		// ��3����������ɵı�־
		if (columnIndex == 3) {
			String task = (String) data[rowIndex][1];
			long time = 0L;

			if (task.equals(aday.cur)) {// ��ɵ��ǵ�ǰ����
				time = aday.finishCur(now);
			} else if (finisher.showFinishDialog()) {// ��ɵĲ��ǵ�ǰ����
				time = finisher.getFinishTime();
				aday.finish(task, time);
			} else
				return;// �û��Լ�ȡ���˴˴���ɲ���

			// �������ѡ����ѡ��״̬
			data[rowIndex][0] = false;
			data[rowIndex][2] = HMS(time);
			data[rowIndex][3] = true;
			this.fireTableRowsUpdated(rowIndex, rowIndex);
		}

		// �������ǵ�0��,����aValue��״̬�ɿ����ǿ�ʼһ����������ͣһ������
		else if ((Boolean) aValue) {// ����һ������
			// ����б����������ִ��,��ͣ��
			if (aday.isWorking()) {
				int i;
				for (i = 0; i < data.length; ++i)
					if ((Boolean) data[i][0]) {
						data[i][0] = false;
						data[i][2] = HMS(aday.stopTask(now));
						break;
					}
				this.fireTableRowsUpdated(i, i);
			}

			// ����������
			aday.startTask((String) data[rowIndex][1], now);
		} else
			// ��ͣһ������
			data[rowIndex][2] = HMS(aday.stopTask(now));

		data[rowIndex][columnIndex] = aValue;

		// ��ǩ������Ϊ�˴�table���޸Ķ��仯��С
		father.updateFromMem();

		// �ø��½���Ĳ�����setValueAt�¼����֮�����,
		// �����Ͳ�����Ϊ���½����ʱ�����µ�������Сʱ��ִ��δ��ɵ�setValueAt�¼�
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				updater.update();
			}

		});
	}

}
