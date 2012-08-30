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

	// 以下几个是组件
	/**
	 * 向上的按钮
	 */
	protected JButton upButton;
	/**
	 * 向下的下拉选项框
	 */
	protected JComboBox<String> downBox;
	/**
	 * 平移的下拉选项框
	 */
	protected JComboBox<String> floorBox;
	/**
	 * 一个放置一个表格的JPanel<br>
	 * 这个JPanel要设border介绍当前表格是什么时候什么阶段的表格
	 */
	protected JPanel tablePane;
	/**
	 * 当前的任务表格,用于继承表宽
	 */
	protected TopTaskTable<? extends Task> curTable;

	// 一下几个是与运算相关的几个变量
	/**
	 * 当前各任务集合的集成对象
	 */
	protected ADay aday;
	/**
	 * 指示各数组的当前下标
	 */
	protected int cur;
	/**
	 * 用于在newView中计算当前的表格的概要字符串
	 */
	protected Calendar curDate;

	// 以下几个是数组
	/**
	 * 任务表格数组
	 * <p>
	 * <b>从低到高依次是年\月\周\日任务集合
	 */
	protected ArrayList<TopTaskTable<? extends Task>> tables;
	/**
	 * 描述-日期 的Map表示的本任务的兄弟们的数组
	 */
	protected ArrayList<TreeMap<String, Calendar>> brothers;
	/**
	 * 下拉列表中被选中的item
	 */
	protected String[] selected;
	/**
	 * 数组的长度都是LEN
	 */
	protected static final int LEN = 4;

	/**
	 * 构造编辑窗体
	 * 
	 * @param owner
	 *            编辑窗体的拥有者
	 */
	public EditorDialog(Window owner) {
		super(owner, "终极编辑窗体", ModalityType.DOCUMENT_MODAL);

		// 获得今日的日期
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		this.curDate = cal;

		aday = new ADay(cal);// 生成编辑窗体的任务集合的集合

		buildTables();// 生成tables

		int i;
		// 获得兄弟集合和选中项
		brothers = new ArrayList<TreeMap<String, Calendar>>(LEN);
		selected = new String[LEN];
		for (i = 0; i < LEN; ++i) {
			TaskMap<? extends Task, ? extends Task> tasks = this.getTasks(i);
			brothers.add(tasks.getBrothers(cal));
			selected[i] = tasks.getItemByCal(cal);
		}

		cur = LEN - 1;
		this.putComponents();// 生成并放置组件

		// 将日表格装入tablePane
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
		locOnCenter(this);// EditorDialog的初始显示位置为屏幕的中心
	}

	/**
	 * 依次构造tables各阶段表格
	 */
	protected void buildTables() {
		tables = new ArrayList<TopTaskTable<? extends Task>>(LEN);
		TopTaskTable<? extends Task> table;

		TaskDialog dialog = new TaskDialog(this);// 生成本对话框的单任务编辑对话框

		// 生成年表格
		table = new TopTaskTable<YearTask>(YearTask.class, dialog, 0, false,
				this, null, new TopTaskModel<YearTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<YearTask, ? extends Task> getTasks() {
						return this.aday.year;
					}
				});
		tables.add(table);

		// 生成月表格
		table = new TopTaskTable<MonthTask>(MonthTask.class, dialog, 0, false,
				this, table, new TopTaskModel<MonthTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<MonthTask, ? extends Task> getTasks() {
						return this.aday.month;
					}

				});
		tables.add(table);

		// 生成周表格
		table = new TopTaskTable<WeekTask>(WeekTask.class, dialog, 0, false,
				this, table, new TopTaskModel<WeekTask>(this.aday) {
					private static final long serialVersionUID = 1L;

					@Override
					public TaskMap<WeekTask, ? extends Task> getTasks() {
						return this.aday.week;
					}

				});
		tables.add(table);

		// 生成日表格
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
	 * 生成并放置组件
	 */
	protected void putComponents() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// 向上按钮
		upButton = new JButton("up");
		upButton.addActionListener(this);
		layout.setConstraints(upButton, c);
		add(upButton);

		// 向下下拉框
		downBox = new JComboBox<String>();
		downBox.addActionListener(this);
		downBox.setEnabled(false);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(downBox, c);
		add(downBox);

		// 表格
		tablePane = new JPanel();
		tablePane.setLayout(new BorderLayout());
		c.fill = GridBagConstraints.BOTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 1.0;
		c.weighty = 1.0;
		layout.setConstraints(tablePane, c);
		add(tablePane);

		// 平行下拉列表
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
	 * 获得位置i的任务集合(在Today对象中)
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
	 * 用新的日期对象构造位置i的任务集合(在Today对象中)
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
	 * 重新布局编辑窗体<br>
	 * 窗体中表格可能已经被替换(cur改变了),实现替换
	 */
	protected void newView() {
		// 继承大小,后面会调用update调整高,所以这里只继承了宽
		Dimension size = curTable.getSize();
		curTable = tables.get(cur);
		curTable.setSize(size);

		// 移除旧表格,装载新表格
		tablePane.removeAll();
		tablePane.add(curTable.getTableHeader(), "North");
		tablePane.add(curTable, "Center");
		tablePane.setBorder(new TitledBorder(this.getTasks(cur).getPanelBorder(
				this.curDate)));

		// 设置floorBox
		// 改变floorBox前移除Action侦听器,以免导致递归调回newView而死循环,最终崩溃
		floorBox.removeActionListener(this);
		floorBox.removeAllItems();
		floorBox.setModel(new DefaultComboBoxModel<String>(new Vector<String>(
				brothers.get(cur).keySet())));
		floorBox.setSelectedItem(selected[cur]);
		floorBox.addActionListener(this);

		// 设置downBox
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

		// 设置upButton
		upButton.setEnabled(cur != 0);
		update();
	}

	@Override
	public void update() {
		// 按照现在表格的行数,设置表格显示的最佳高度
		Dimension size = curTable.getSize();
		size.height = curTable.getRowCount() * curTable.getRowHeight();
		curTable.setPreferredSize(size);

		pack();
	}

	/**
	 * 现在选中brothers[cur]作为当前表格,更新一些数据
	 * 
	 * @param s
	 *            选中项
	 */
	protected void curSelect(String s) {
		selected[cur] = s; // 设置选中数组
		curDate = brothers.get(cur).get(s);// 获得选中项指示的日期

		this.setTasks(cur, curDate); // 读取新任务集合
		tables.get(cur).updateJustMe();// 更新表格显示

		// 更新brothers[cur+1],父亲不同了孩子也就不同了
		if (cur + 1 != LEN)
			brothers.set(cur + 1, this.getTasks(cur + 1).getBrothers(curDate));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == upButton)// 向上
			cur--;
		else if (source == downBox) {// 向下
			String s = (String) downBox.getSelectedItem();
			cur++;
			curSelect(s);
		} else {
			String s = (String) floorBox.getSelectedItem();
			curSelect(s);
		}

		newView(); // 根据成员变量的值,设置新界面
	}
}
