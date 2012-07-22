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

import static gui.FormatTime.*;

public class Top extends JDialog implements ActionListener, Updater {
	private static final long serialVersionUID = 1L;

	/**
	 * 包含当前运行所需数据
	 */
	protected Today today;
	/**
	 * 今天的年/月/日 星期
	 */
	protected String date;
	/**
	 * 显示整体信息
	 */
	protected JLabel info;
	/**
	 * 给标签提供的右键菜单
	 */
	protected JPopupMenu menu;
	/**
	 * 移动开始时设置的点
	 */
	protected Point origin;
	/**
	 * 始终置顶可选框
	 */
	protected JCheckBoxMenuItem alwaysTop;
	/**
	 * 编辑窗体
	 */
	protected EditorDialog editor;
	/**
	 * 标签右键菜单中的"编辑"项
	 */
	protected JMenuItem editorMenu;
	/**
	 * "今日"表格
	 */
	protected TodayTable todayTable;
	/**
	 * "明天","本周","本月","本年"表格, 用于调整表格的宽度,别无所求
	 */
	protected LinkedList<TopTaskTable<? extends Task>> others;
	/**
	 * 用于放置"明天","本周","本月","本年"表格, 如果"最大"的话,位于界面的"South"
	 */
	protected JPanel othersPanel;
	/**
	 * 是othersPanel中的最后一个放置的JPanel组件<br>
	 * 用它来计算Border的宽度
	 */
	protected JPanel tablePanel;
	/**
	 * 包含一个表格的JPanel的Border的宽度
	 */
	protected int widthofBorder;

	// 显示的3种模式:"最大","一般","最小"
	protected static final int BIG = 0;
	protected static final int NORMAL = 1;
	protected static final int MINI = 2;
	/**
	 * 单个任务编辑对话框
	 */
	protected TaskDialog dialog;

	public Top() {
		this.setTitle("今日事今日毕");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		// 生成运行数据
		today = new Today(cal);

		// 生成日期
		String[] week = { "日", "一", "二", "三", "四", "五", "六" };
		date = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1)
				+ "/" + cal.get(Calendar.DATE) + " "
				+ week[cal.get(Calendar.DAY_OF_WEEK) - 1];

		// 创建显示组件
		info = new JLabel("");
		updateLabel();
		buildMenu();

		// 添加标签的点击事件,点击一次更新一次
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

		dialog = new TaskDialog(this);// 单任务编辑器

		this.buildTables();// 建立各级表格
		this.packOthers();// 将others放置到othersPanel中

		add(todayTable, "Center");
		pack();// 目的是让指示宽度的todayTable有个宽度
		changeShowMode(BIG);// 重新装载各个组件

		// 计算出Border的宽度
		this.widthofBorder = tablePanel.getSize().width
				- others.getFirst().getSize().width;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				editor = new EditorDialog(Top.this);
				editorMenu.setEnabled(true);
			}
		});

		this.setLocation(200, 200);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * 建立各种表格
	 */
	protected void buildTables() {
		others = new LinkedList<TopTaskTable<? extends Task>>();
		TopTaskTable<? extends Task> table;

		// 生成年表格
		table = new TopTaskTable<YearTask>(YearTask.class, dialog, 0, false,
				this, null, new TopTaskModel<YearTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<YearTask, ? extends Task> getTasks() {
						return this.aday.year;
					}
				});
		others.add(table);

		// 生成月表格
		table = new TopTaskTable<MonthTask>(MonthTask.class, dialog, 0, false,
				this, table, new TopTaskModel<MonthTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<MonthTask, ? extends Task> getTasks() {
						return this.aday.month;
					}

				});
		others.add(table);

		// 生成周表格
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

		// 生成"今日"表格
		this.todayTable = new TodayTable(dialog, this, week, new TodayModel(
				today, this, week));

		// 生成"明日"表格
		table = new TopTaskTable<DayTask>(DayTask.class, dialog, 0, false,
				this, week, new TopTaskModel<DayTask>(this.today) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<DayTask, ? extends Task> getTasks() {
						return this.aday.tomorrow;
					}

				});
		others.add(table);
	}

	/**
	 * 放置others中的表格到othersPanel中
	 */
	protected void packOthers() {
		// 设置othersPanel
		othersPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		othersPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;

		// 逆序将others装入othersPanel
		String[] borders = new String[] { "明天", "本周", "本月", "今年" };
		Iterator<TopTaskTable<? extends Task>> it = others.descendingIterator();
		for (int i = 0; it.hasNext(); ++i) {
			// 将一个表格放到一个新的JPanel中
			JTable table = it.next();
			tablePanel = new JPanel();
			tablePanel.setBorder(new TitledBorder(borders[i]));
			tablePanel.setLayout(new BorderLayout());
			tablePanel.add(table.getTableHeader(), "North");
			tablePanel.add(table, "Center");

			if (!it.hasNext())// 如果是最后一个表格了,封闭
				c.gridheight = GridBagConstraints.REMAINDER;
			layout.setConstraints(tablePanel, c);
			othersPanel.add(tablePanel);
		}
	}

	/**
	 * 更新标签的显示
	 */
	protected void updateLabel() {
		Date now = new Date();
		String text = "<html>" + date + "<br>";
		String start = "开机运行" + HMS(now.getTime() - today.getStartTime());
		String total = "今日总任务" + HMS(today.day.getTotal());
		String complete = "今日已完成" + HMS(today.day.getFinished());
		String vacancy = "空闲了"
				+ HMS(today.isWorking() ? today.vacancy : today.vacancy
						+ now.getTime() - today.startLazy.getTime());
		text += start + "<br>" + total + "<br>" + complete + "<br>" + vacancy;
		if (today.isWorking()) {
			text += "<br>当前任务已用" + HMS(today.getCurUsed());
		}
		info.setText(text);
	}

	/**
	 * 建立标签的右键菜单
	 */
	protected void buildMenu() {
		menu = new JPopupMenu();

		String[] cmds = { "编辑", "置顶", "退出", "最大", "一般", "最小" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			if (cmds[i].equals("置顶")) {
				alwaysTop = new JCheckBoxMenuItem(cmds[i]);
				alwaysTop.addActionListener(this);
				menu.add(alwaysTop);
				continue;
			}
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
			if (cmds[i].equals("编辑")) {
				t.setEnabled(false);
				this.editorMenu = t;
			}
			if (i == 2)
				menu.addSeparator();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("退出")) {
			this.dispose();
		} else if (cmd.equals("最大")) {
			this.changeShowMode(BIG);
		} else if (cmd.equals("一般")) {
			this.changeShowMode(NORMAL);
		} else if (cmd.equals("最小")) {
			this.changeShowMode(MINI);
		} else if (cmd.equals("置顶")) {
			this.setAlwaysOnTop(alwaysTop.isSelected());
		} else if (cmd.equals("编辑")) {
			editor.setVisible(true);
			todayTable.updateFromFile();// 更新"今日"的上溯所有表格
			others.getLast().updateFromFile();// 更新"明日"的上溯所有表格
			this.update();
		}
	}

	@Override
	public void update() {
		// 更新todayTable的大小
		int width = todayTable.getSize().width;

		/*
		 * 因为TodayTable没有"总计"行,所以可能一行数据都没有,
		 * <br>就将TodayTable的大小设置为至少一行的高度,以方便用户点击
		 */
		int rows = todayTable.getRowCount();
		rows = rows == 0 ? 1 : rows;
		todayTable.setPreferredSize(new Dimension(width, rows
				* todayTable.getRowHeight()));

		// 减去Border的宽度
		width -= this.widthofBorder;

		// 更新others中各表格的高
		for (JTable table : others)
			table.setPreferredSize(new Dimension(width, table.getRowCount()
					* table.getRowHeight()));
		// 更新标签
		this.updateLabel();
		pack();
	}

	/**
	 * 改为mode显示模式<br>
	 * mode的取值为BIG\NORMAL\MINI中的一个
	 * 
	 * @param mode
	 */
	protected void changeShowMode(int mode) {
		dispose();// 可能要设置undecorated,必须先让界面不可见
		this.getContentPane().removeAll();// 清除内容面板内所有组件

		// 添加info到"North"
		this.add(info, "North");

		switch (mode) {
		case BIG:
			this.add(othersPanel, "South");
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
