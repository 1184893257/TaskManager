package gui;

import inter.Updater;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import data.*;
import data.task.*;
import data.tasks.TaskMap;

import static gui.StaticMethod.*;

public class Top extends JDialog implements ActionListener, Updater {
	private static final long serialVersionUID = 1L;

	/**
	 * ������ǰ������������
	 */
	protected Today today;
	/**
	 * �������/��/�� ����
	 */
	protected String date;
	/**
	 * ��ʾ������Ϣ
	 */
	protected JLabel info;
	/**
	 * ����ǩ�ṩ���Ҽ��˵�
	 */
	protected JPopupMenu menu;
	/**
	 * �ƶ���ʼʱ���õĵ�
	 */
	protected Point origin;
	/**
	 * ʼ���ö���ѡ��
	 */
	protected JCheckBoxMenuItem alwaysTop;
	/**
	 * �༭����
	 */
	protected EditorDialog editor;
	/**
	 * ��ǩ�Ҽ��˵��е�"�༭"��
	 */
	protected JMenuItem editorMenu;

	// �����Ƕ��������,����װ���ǵ�JPanel
	/**
	 * "����"���
	 */
	protected TodayTable todayTable;
	/**
	 * "����"���
	 */
	protected TopTaskTable<DayTask> tomorrow;
	/**
	 * ����"����"����JPanel
	 */
	protected JPanel tomorrowPanel;
	/**
	 * "����","����","����"���, ���ڵ������Ŀ��,��������
	 */
	protected LinkedList<TopTaskTable<? extends Task>> others;
	/**
	 * ���ڷ���"����","����","����"���, ���"���"�Ļ�,λ�ڽ����"South"
	 */
	protected JPanel othersPanel;
	/**
	 * ��װ����Top��"Center"��JPanel<br>
	 * ��"���"ģʽ��,װ�����б��
	 */
	protected JPanel center;

	// center�и����ֵĲ��ֲ���
	protected GridBagConstraints tod;
	protected GridBagConstraints tom;
	protected GridBagConstraints oth;

	// ��ʾ��3��ģʽ:"���","һ��","��С"
	protected static final int BIG = 0;
	protected static final int NORMAL = 1;
	protected static final int MINI = 2;
	/**
	 * JPanel����Border�����ӵĿ��
	 */
	protected final int widthofBorder;

	public Top() {
		this.setTitle("�����½��ձ�");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		// ������������
		today = new Today(cal);

		// ��������
		String[] week = { "��", "һ", "��", "��", "��", "��", "��" };
		date = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1)
				+ "/" + cal.get(Calendar.DATE) + " "
				+ week[cal.get(Calendar.DAY_OF_WEEK) - 1];

		// ������ʾ���
		info = new JLabel("");
		updateLabel();
		buildMenu();

		// ��ӱ�ǩ�ĵ���¼�,���һ�θ���һ��
		info.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				origin = e.getPoint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				Top.this.update();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					menu.show(e.getComponent(), e.getX(), e.getY());
			}

		});

		info.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = Top.this.getLocation();
				Top.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
						- origin.y);
			}

		});

		// ���ɱ��,��װ���
		this.buildTables();// �����������
		this.packTables();// �Ա����б�Ҫ�İ�װ

		// ����"���"ģʽ������,�Լ�һЩ�����Ĵ���
		center = new JPanel();
		center.setLayout(new GridBagLayout());
		setConstr();// ����tod\tom\oth���ֲ���,�ֱ���"����","����",���������õ�center�еĲ���

		// ��ʼ��ʾģʽ
		add(todayTable, "Center");
		pack();// Ŀ������ָʾ��ȵ�todayTable�и����
		changeShowMode(BIG);// ����װ�ظ������

		// ������Border�����ӵĿ��
		this.widthofBorder = this.tomorrowPanel.getSize().width
				- this.tomorrow.getSize().width;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				editor = new EditorDialog(Top.this);
				editorMenu.setEnabled(true);
			}
		});

		locOnCenter(this);// Top�ĳ�ʼ��ʾλ��Ϊ��Ļ������
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * ����tod\tom\oth���ֲ���
	 */
	protected void setConstr() {
		// "����"�Ĳ��ֲ���
		tod = new GridBagConstraints();
		tod.fill = GridBagConstraints.HORIZONTAL;
		tod.anchor = GridBagConstraints.NORTH;
		tod.weightx = 0.5;

		// "����"�Ĳ��ֲ���
		tom = (GridBagConstraints) tod.clone();
		tom.gridy = 1;
		tom.gridheight = GridBagConstraints.REMAINDER;

		// "����"�Ĳ��ֲ���
		oth = (GridBagConstraints) tod.clone();
		oth.gridx = 1;
		oth.gridheight = GridBagConstraints.REMAINDER;
		oth.gridwidth = GridBagConstraints.REMAINDER;
	}

	/**
	 * �������ֱ��
	 */
	protected void buildTables() {
		others = new LinkedList<TopTaskTable<? extends Task>>();
		TopTaskTable<? extends Task> table;

		TaskDialog dialog = new TaskDialog(this);// ������༭��

		// ��������
		table = new TopTaskTable<YearTask>(YearTask.class, dialog, 0, false,
				this, null, new TopTaskModel<YearTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<YearTask, ? extends Task> getTasks() {
						return this.aday.year;
					}
				});
		others.add(table);

		// �����±��
		table = new TopTaskTable<MonthTask>(MonthTask.class, dialog, 0, false,
				this, table, new TopTaskModel<MonthTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<MonthTask, ? extends Task> getTasks() {
						return this.aday.month;
					}

				});
		others.add(table);

		// �����ܱ��
		table = new TopTaskTable<WeekTask>(WeekTask.class, dialog, 0, false,
				this, table, new TopTaskModel<WeekTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<WeekTask, ? extends Task> getTasks() {
						return this.aday.week;
					}

				});
		others.add(table);

		@SuppressWarnings("unchecked")
		TopTaskTable<WeekTask> week = (TopTaskTable<WeekTask>) table;

		// ����"����"���
		this.todayTable = new TodayTable(dialog, this, week, new TodayModel(
				this, today, this, week));

		// ����"����"���
		this.tomorrow = new TopTaskTable<DayTask>(DayTask.class, dialog, 0,
				false, this, week, new TopTaskModel<DayTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<DayTask, ? extends Task> getTasks() {
						return this.aday.tomorrow;
					}

				});
	}

	/**
	 * ����һ����װtable����BorderΪborder��JPanel
	 * 
	 * @param table
	 * @param border
	 * @return
	 */
	protected JPanel newTablePanel(JTable table, String border) {
		JPanel ans = new JPanel();
		ans.setBorder(new TitledBorder(border));
		ans.setLayout(new BorderLayout());
		table.getTableHeader().setPreferredSize(
				table.getTableHeader().getMinimumSize());
		ans.add(table.getTableHeader(), "North");
		ans.add(table, "Center");
		return ans;
	}

	/**
	 * ����others�еı��othersPanel��<br>
	 * �����ձ����õ�tomorrrowPanel��
	 */
	protected void packTables() {
		// ����othersPanel
		othersPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		othersPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;

		// ����othersװ��othersPanel
		String[] borders = new String[] { "����", "����", "����" };
		Iterator<TopTaskTable<? extends Task>> it = others.descendingIterator();
		for (int i = 0; it.hasNext(); ++i) {
			// ��һ�����ŵ�һ���µ�JPanel��
			JTable table = it.next();
			JPanel tablePanel = this.newTablePanel(table, borders[i]);

			if (!it.hasNext())// ��������һ�������,���
				c.gridheight = GridBagConstraints.REMAINDER;
			layout.setConstraints(tablePanel, c);
			othersPanel.add(tablePanel);
		}

		this.tomorrowPanel = this.newTablePanel(tomorrow, "����");
	}

	/**
	 * ���±�ǩ����ʾ
	 */
	protected void updateLabel() {
		Date now = new Date();
		String text = "<html>" + date + "<br>";
		String start = "��������" + HMS(now.getTime() - today.getStartTime());
		String total = "����������" + HMS(today.day.getTotal());
		String complete = "���������" + HMS(today.day.getFinished());
		String vacancy = "������"
				+ HMS(today.isWorking() ? today.vacancy : today.vacancy
						+ now.getTime() - today.startLazy.getTime());
		text += start + "<br>" + total + "<br>" + complete + "<br>" + vacancy;
		if (today.isWorking()) {
			text += "<br>��ǰ��������" + HMS(today.getCurUsed());
		}
		info.setText(text);
	}

	/**
	 * ������ǩ���Ҽ��˵�
	 */
	protected void buildMenu() {
		menu = new JPopupMenu();

		String[] cmds = { "�༭", "�ö�", "���", "һ��", "��С" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			if (cmds[i].equals("�ö�"))
				t = alwaysTop = new JCheckBoxMenuItem("�ö�");
			else
				t = new JMenuItem(cmds[i]);

			t.addActionListener(this);
			menu.add(t);

			if (cmds[i].equals("�ö�"))
				menu.addSeparator();
			else if (cmds[i].equals("�༭")) {
				t.setEnabled(false);
				this.editorMenu = t;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("���")) {
			this.changeShowMode(BIG);
		} else if (cmd.equals("һ��")) {
			this.changeShowMode(NORMAL);
		} else if (cmd.equals("��С")) {
			this.changeShowMode(MINI);
		} else if (cmd.equals("�ö�")) {
			this.setAlwaysOnTop(alwaysTop.isSelected());
		} else if (cmd.equals("�༭")) {
			editor.setVisible(true);
			todayTable.updateFromFile();// ����"����"���������б��

			// �������ձ��
			today.tomorrow.updateFromFile();
			tomorrow.updateJustMe();

			this.update();
		}
	}

	@Override
	public void update() {
		// ����todayTable�Ĵ�С
		int width = todayTable.getSize().width;

		/*
		 * ��ΪTodayTableû��"�ܼ�"��,���Կ���һ�����ݶ�û��,
		 * <br>�ͽ�TodayTable�Ĵ�С����Ϊ����һ�еĸ߶�,�Է����û����
		 */
		int rows = todayTable.getRowCount();
		rows = rows == 0 ? 1 : rows;
		todayTable.setPreferredSize(new Dimension(width, rows
				* todayTable.getRowHeight()));

		// ��ȥBorder���ӵĿ��
		width -= this.widthofBorder;

		// ����"����"����PreferredSize
		tomorrow.setPreferredSize(tomorrow.getMinimumSize());

		// ����others������PreferredSize
		for (JTable table : others)
			table.setPreferredSize(new Dimension(width, table.getRowCount()
					* table.getRowHeight()));

		// ���±�ǩ
		this.updateLabel();
		pack();
		pack();
	}

	/**
	 * ��Ϊmode��ʾģʽ<br>
	 * mode��ȡֵΪBIG\NORMAL\MINI�е�һ��
	 * 
	 * @param mode
	 */
	protected void changeShowMode(int mode) {
		dispose();// ����Ҫ����undecorated,�������ý��治�ɼ�
		this.getContentPane().removeAll();// �������������������

		// ���info��"North"
		this.add(info, "North");

		switch (mode) {
		case BIG:
			center.removeAll();
			GridBagLayout layout = (GridBagLayout) center.getLayout();

			layout.setConstraints(todayTable, tod);
			center.add(todayTable);

			layout.setConstraints(tomorrowPanel, tom);
			center.add(tomorrowPanel);

			layout.setConstraints(othersPanel, oth);
			center.add(othersPanel);

			this.add(center, "Center");
			break;
		case NORMAL:
			this.add(todayTable, "Center");
			break;
		}
		this.setUndecorated(mode != BIG);
		update();
		this.setVisible(true);
	}

	public static void main(String[] args) {
		Top t = new Top();
		t.setVisible(true);
	}
}
