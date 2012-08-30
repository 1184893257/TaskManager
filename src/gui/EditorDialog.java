package gui;

import static gui.StaticMethod.locOnCenter;
import inter.Updater;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import data.*;
import data.task.*;
import data.tasks.TaskMap;

public class EditorDialog extends JDialog implements Updater, ActionListener {
	private static final long serialVersionUID = 1L;

	// ���¼��������
	/**
	 * ���ϵİ�ť
	 */
	protected JButton upButton;
	/**
	 * ���µ�����ѡ���
	 */
	protected JComboBox<String> downBox;
	/**
	 * ƽ�Ƶ�����ѡ���
	 */
	protected JComboBox<String> floorBox;
	/**
	 * һ������һ������JPanel<br>
	 * ���JPanelҪ��border���ܵ�ǰ�����ʲôʱ��ʲô�׶εı��
	 */
	protected JPanel tablePane;
	/**
	 * ��ǰ��������,���ڼ̳б��
	 */
	protected TopTaskTable<? extends Task> curTable;

	// һ�¼�������������صļ�������
	/**
	 * ��ǰ�����񼯺ϵļ��ɶ���
	 */
	protected ADay aday;
	/**
	 * ָʾ������ĵ�ǰ�±�
	 */
	protected int cur;
	/**
	 * ������newView�м��㵱ǰ�ı��ĸ�Ҫ�ַ���
	 */
	protected Calendar curDate;

	// ���¼���������
	/**
	 * ����������
	 * <p>
	 * <b>�ӵ͵�����������\��\��\�����񼯺�
	 */
	protected ArrayList<TopTaskTable<? extends Task>> tables;
	/**
	 * ����-���� ��Map��ʾ�ı�������ֵ��ǵ�����
	 */
	protected ArrayList<TreeMap<String, Calendar>> brothers;
	/**
	 * �����б��б�ѡ�е�item
	 */
	protected String[] selected;
	/**
	 * ����ĳ��ȶ���LEN
	 */
	protected static final int LEN = 4;

	/**
	 * ����༭����
	 * 
	 * @param owner
	 *            �༭�����ӵ����
	 */
	public EditorDialog(Window owner) {
		super(owner, "�ռ��༭����", ModalityType.DOCUMENT_MODAL);

		// ��ý��յ�����
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		this.curDate = cal;

		aday = new ADay(cal);// ���ɱ༭��������񼯺ϵļ���

		buildTables();// ����tables

		int i;
		// ����ֵܼ��Ϻ�ѡ����
		brothers = new ArrayList<TreeMap<String, Calendar>>(LEN);
		selected = new String[LEN];
		for (i = 0; i < LEN; ++i) {
			TaskMap<? extends Task, ? extends Task> tasks = this.getTasks(i);
			brothers.add(tasks.getBrothers(cal));
			selected[i] = tasks.getItemByCal(cal);
		}

		cur = LEN - 1;
		this.putComponents();// ���ɲ��������

		// ���ձ��װ��tablePane
		this.curTable = tables.get(cur);
		tablePane.add(curTable.getTableHeader(), "North");
		tablePane.add(curTable, "Center");
		tablePane.setBorder(new TitledBorder(this.getTasks(cur).getPanelBorder(
				cal)));

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				tables.get(LEN - 1).updateFromFile();
				EditorDialog.this.update();
				super.componentShown(e);
			}

		});
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		locOnCenter(this);// EditorDialog�ĳ�ʼ��ʾλ��Ϊ��Ļ������
	}

	/**
	 * ���ι���tables���׶α��
	 */
	protected void buildTables() {
		tables = new ArrayList<TopTaskTable<? extends Task>>(LEN);
		TopTaskTable<? extends Task> table;

		TaskDialog dialog = new TaskDialog(this);// ���ɱ��Ի���ĵ�����༭�Ի���

		// ��������
		table = new TopTaskTable<YearTask>(YearTask.class, dialog, 0, false,
				this, null, new TopTaskModel<YearTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<YearTask, ? extends Task> getTasks() {
						return this.aday.year;
					}
				});
		tables.add(table);

		// �����±��
		table = new TopTaskTable<MonthTask>(MonthTask.class, dialog, 0, false,
				this, table, new TopTaskModel<MonthTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<MonthTask, ? extends Task> getTasks() {
						return this.aday.month;
					}

				});
		tables.add(table);

		// �����ܱ��
		table = new TopTaskTable<WeekTask>(WeekTask.class, dialog, 0, false,
				this, table, new TopTaskModel<WeekTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<WeekTask, ? extends Task> getTasks() {
						return this.aday.week;
					}

				});
		tables.add(table);

		// �����ձ��
		table = new TopTaskTable<DayTask>(DayTask.class, dialog, 0, false,
				this, table, new TopTaskModel<DayTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<DayTask, ? extends Task> getTasks() {
						return this.aday.day;
					}

				});
		tables.add(table);
	}

	/**
	 * ���ɲ��������
	 */
	protected void putComponents() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// ���ϰ�ť
		upButton = new JButton("up");
		upButton.addActionListener(this);
		layout.setConstraints(upButton, c);
		add(upButton);

		// ����������
		downBox = new JComboBox<String>();
		downBox.addActionListener(this);
		downBox.setEnabled(false);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(downBox, c);
		add(downBox);

		// ���
		tablePane = new JPanel();
		tablePane.setLayout(new BorderLayout());
		c.fill = GridBagConstraints.BOTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 1.0;
		c.weighty = 1.0;
		layout.setConstraints(tablePane, c);
		add(tablePane);

		// ƽ�������б�
		floorBox = new JComboBox<String>(new Vector<String>(brothers.get(cur)
				.keySet()));
		floorBox.setSelectedItem(selected[cur]);
		floorBox.addActionListener(this);
		c.fill = GridBagConstraints.NONE;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		layout.setConstraints(floorBox, c);
		add(floorBox);
	}

	/**
	 * ���λ��i�����񼯺�(��Today������)
	 * 
	 * @param i
	 * @return
	 */
	protected TaskMap<? extends Task, ? extends Task> getTasks(int i) {
		TaskMap<? extends Task, ? extends Task> ans = null;
		switch (i) {
		case 0:
			ans = aday.year;
			break;
		case 1:
			ans = aday.month;
			break;
		case 2:
			ans = aday.week;
			break;
		case 3:
			ans = aday.day;
			break;
		}
		return ans;
	}

	/**
	 * ���µ����ڶ�����λ��i�����񼯺�(��Today������)
	 * 
	 * @param i
	 * @param cal
	 */
	protected void setTasks(int i, Calendar cal) {
		switch (i) {
		case 0:
			aday.setYear(cal);
			break;
		case 1:
			aday.setMonth(cal);
			break;
		case 2:
			aday.setWeek(cal);
			break;
		case 3:
			aday.setDay(cal);
			break;
		}
	}

	/**
	 * ���²��ֱ༭����<br>
	 * �����б������Ѿ����滻(cur�ı���),ʵ���滻
	 */
	protected void newView() {
		// �̳д�С,��������update������,��������ֻ�̳��˿�
		Dimension size = curTable.getSize();
		curTable = tables.get(cur);
		curTable.setSize(size);

		// �Ƴ��ɱ��,װ���±��
		tablePane.removeAll();
		tablePane.add(curTable.getTableHeader(), "North");
		tablePane.add(curTable, "Center");
		tablePane.setBorder(new TitledBorder(this.getTasks(cur).getPanelBorder(
				this.curDate)));

		// ����floorBox
		// �ı�floorBoxǰ�Ƴ�Action������,���⵼�µݹ����newView����ѭ��,���ձ���
		floorBox.removeActionListener(this);
		floorBox.removeAllItems();
		floorBox.setModel(new DefaultComboBoxModel<String>(new Vector<String>(
				brothers.get(cur).keySet())));
		floorBox.setSelectedItem(selected[cur]);
		floorBox.addActionListener(this);

		// ����downBox
		if (cur + 1 == LEN)
			downBox.setEnabled(false);
		else {
			downBox.removeActionListener(this);
			downBox.removeAllItems();
			downBox.setModel(new DefaultComboBoxModel<String>(
					new Vector<String>(brothers.get(cur + 1).keySet())));
			downBox.setEnabled(true);
			downBox.setSelectedItem(selected[cur + 1]);
			downBox.addActionListener(this);
		}

		// ����upButton
		upButton.setEnabled(cur != 0);
		update();
	}

	@Override
	public void update() {
		// �������ڱ�������,���ñ����ʾ����Ѹ߶�
		Dimension size = curTable.getSize();
		size.height = curTable.getRowCount() * curTable.getRowHeight();
		curTable.setPreferredSize(size);

		pack();
	}

	/**
	 * ����ѡ��brothers[cur]��Ϊ��ǰ���,����һЩ����
	 * 
	 * @param s
	 *            ѡ����
	 */
	protected void curSelect(String s) {
		selected[cur] = s; // ����ѡ������
		curDate = brothers.get(cur).get(s);// ���ѡ����ָʾ������

		this.setTasks(cur, curDate); // ��ȡ�����񼯺�
		tables.get(cur).updateJustMe();// ���±����ʾ

		// ����brothers[cur+1],���ײ�ͬ�˺���Ҳ�Ͳ�ͬ��
		if (cur + 1 != LEN)
			brothers.set(cur + 1, this.getTasks(cur + 1).getBrothers(curDate));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == upButton)// ����
			cur--;
		else if (source == downBox) {// ����
			String s = (String) downBox.getSelectedItem();
			cur++;
			curSelect(s);
		} else {
			String s = (String) floorBox.getSelectedItem();
			curSelect(s);
		}

		newView(); // ���ݳ�Ա������ֵ,�����½���
	}
}
