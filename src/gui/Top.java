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

	// 以下是多个表格组件,及包装它们的JPanel
	/**
	 * "今日"表格
	 */
	protected TodayTable todayTable;
	/**
	 * "明天"表格
	 */
	protected TopTaskTable<DayTask> tomorrow;
	/**
	 * 包含"明天"表格的JPanel
	 */
	protected JPanel tomorrowPanel;
	/**
	 * "本周","本月","本年"表格, 用于调整表格的宽度,别无所求
	 */
	protected LinkedList<TopTaskTable<? extends Task>> others;
	/**
	 * 用于放置"本周","本月","本年"表格, 如果"最大"的话,位于界面的"South"
	 */
	protected JPanel othersPanel;
	/**
	 * 被装载入Top的"Center"的JPanel<br>
	 * 在"最大"模式中,装入所有表格
	 */
	protected JPanel center;

	// center中各部分的布局参数
	protected GridBagConstraints tod;
	protected GridBagConstraints tom;
	protected GridBagConstraints oth;

	// 显示的3种模式:"最大","一般","最小"
	protected static final int BIG = 0;
	protected static final int NORMAL = 1;
	protected static final int MINI = 2;
	/**
	 * JPanel设置Border后增加的宽度
	 */
	protected final int widthofBorder;

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

		// 生成表格,包装表格
		this.buildTables();// 建立各级表格
		this.packTables();// 对表格进行必要的包装

		// 生成"最大"模式的容器,以及一些参数的处理
		center = new JPanel();
		center.setLayout(new GridBagLayout());
		setConstr();// 设置tod\tom\oth布局参数,分别是"今天","明天",其他表格放置到center中的参数

		// 初始显示模式
		add(todayTable, "Center");
		pack();// 目的是让指示宽度的todayTable有个宽度
		changeShowMode(BIG);// 重新装载各个组件

		// 求设置Border后增加的宽度
		this.widthofBorder = this.tomorrowPanel.getSize().width
				- this.tomorrow.getSize().width;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				editor = new EditorDialog(Top.this);
				editorMenu.setEnabled(true);
			}
		});

		locOnCenter(this);// Top的初始显示位置为屏幕的中心
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * 设置tod\tom\oth布局参数
	 */
	protected void setConstr() {
		// "今天"的布局参数
		tod = new GridBagConstraints();
		tod.fill = GridBagConstraints.HORIZONTAL;
		tod.anchor = GridBagConstraints.NORTH;
		tod.weightx = 0.5;

		// "明天"的布局参数
		tom = (GridBagConstraints) tod.clone();
		tom.gridy = 1;
		tom.gridheight = GridBagConstraints.REMAINDER;

		// "其他"的布局参数
		oth = (GridBagConstraints) tod.clone();
		oth.gridx = 1;
		oth.gridheight = GridBagConstraints.REMAINDER;
		oth.gridwidth = GridBagConstraints.REMAINDER;
	}

	/**
	 * 建立各种表格
	 */
	protected void buildTables() {
		others = new LinkedList<TopTaskTable<? extends Task>>();
		TopTaskTable<? extends Task> table;

		TaskDialog dialog = new TaskDialog(this);// 单任务编辑器

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
				this, today, this, week));

		// 生成"明日"表格
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
	 * 生成一个包装table并且Border为border的JPanel
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
	 * 放置others中的表格到othersPanel中<br>
	 * 将明日表格放置到tomorrrowPanel中
	 */
	protected void packTables() {
		// 设置othersPanel
		othersPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		othersPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;

		// 逆序将others装入othersPanel
		String[] borders = new String[] { "本周", "本月", "今年" };
		Iterator<TopTaskTable<? extends Task>> it = others.descendingIterator();
		for (int i = 0; it.hasNext(); ++i) {
			// 将一个表格放到一个新的JPanel中
			JTable table = it.next();
			JPanel tablePanel = this.newTablePanel(table, borders[i]);

			if (!it.hasNext())// 如果是最后一个表格了,封闭
				c.gridheight = GridBagConstraints.REMAINDER;
			layout.setConstraints(tablePanel, c);
			othersPanel.add(tablePanel);
		}

		this.tomorrowPanel = this.newTablePanel(tomorrow, "明天");
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

		String[] cmds = { "编辑", "置顶", "最大", "一般", "最小" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			if (cmds[i].equals("置顶"))
				t = alwaysTop = new JCheckBoxMenuItem("置顶");
			else
				t = new JMenuItem(cmds[i]);

			t.addActionListener(this);
			menu.add(t);

			if (cmds[i].equals("置顶"))
				menu.addSeparator();
			else if (cmds[i].equals("编辑")) {
				t.setEnabled(false);
				this.editorMenu = t;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("最大")) {
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

			// 更新明日表格
			today.tomorrow.updateFromFile();
			tomorrow.updateJustMe();

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

		// 减去Border增加的宽度
		width -= this.widthofBorder;

		// 设置"明天"表格的PreferredSize
		tomorrow.setPreferredSize(tomorrow.getMinimumSize());

		// 调整others各表格的PreferredSize
		for (JTable table : others)
			table.setPreferredSize(new Dimension(width, table.getRowCount()
					* table.getRowHeight()));

		// 更新标签
		this.updateLabel();
		pack();
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
