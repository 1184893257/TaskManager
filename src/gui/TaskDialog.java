package gui;

import static gui.StaticMethod.locOnCenter;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import data.TopTaskModel;
import data.task.*;

public class TaskDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * 这次对话框被取消了,内容不可信
	 */
	private boolean canceled;
	/**
	 * 对话框最后的编辑结果(一个任务)
	 */
	public Task task;
	/**
	 * 如果是修改任务,这里存的是正在修改的任务的名称
	 */
	public String modifyInfo;

	// 获取信息的组件
	/**
	 * 填写任务内容的text
	 */
	protected JTextField infoText;
	/**
	 * 填写预计需要时间的小时部分
	 */
	protected JTextField hourText;
	/**
	 * 填写预计需要时间的分钟部分
	 */
	protected JTextField minuteText;
	/**
	 * 可供选择的所有父任务,第一项是无(表示没有父任务)
	 */
	protected JComboBox<String> fathers;

	// 将各种组件封装在JPanel中,支持热插播O(∩_∩)O~
	protected JPanel infoPanel;
	protected JPanel timePanel;
	protected JPanel fatherPanel;
	protected JButton ok;

	public TaskDialog(Window owner) {
		super(owner, ModalityType.DOCUMENT_MODAL);

		this.setLayout(new GridBagLayout());// 设置布局管理器

		this.buildInfoPanel();
		this.buildTimePanel();
		this.buildFatherPanel();

		// 添加确定按钮
		ok = new JButton("确定");
		ok.addActionListener(this);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
				TaskDialog.this.setVisible(false);
			}
		});

		locOnCenter(this);// TaskDialog左上角的初始显示位置为屏幕的中心
	}

	/**
	 * 任务内容插件
	 */
	protected void buildInfoPanel() {
		this.infoPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.infoPanel.setLayout(layout);

		// 添加任务内容文本框
		JLabel label = new JLabel("任务内容:");
		layout.setConstraints(label, c);
		this.infoPanel.add(label);
		infoText = new JTextField("请输入任务内容", 20);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		layout.setConstraints(infoText, c);
		this.infoPanel.add(infoText);
	}

	/**
	 * 时间插件
	 */
	protected void buildTimePanel() {
		this.timePanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.timePanel.setLayout(layout);

		// 添加所需时间标签
		JLabel label = new JLabel("所需时间:");
		layout.setConstraints(label, c);
		this.timePanel.add(label);

		// 添加小时输入框
		hourText = new JTextField("0", 2);
		c.gridx = GridBagConstraints.RELATIVE;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(hourText, c);
		this.timePanel.add(hourText);

		label = new JLabel("小时");
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		this.timePanel.add(label);

		// 添加分钟输入框
		minuteText = new JTextField("3", 2);
		c.weightx = 0.5;
		layout.setConstraints(minuteText, c);
		this.timePanel.add(minuteText);

		label = new JLabel("分钟");
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		this.timePanel.add(label);
	}

	/**
	 * 父任务插件
	 */
	protected void buildFatherPanel() {
		this.fatherPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.fatherPanel.setLayout(layout);

		JLabel label = new JLabel("父任务:");
		layout.setConstraints(label, c);
		this.fatherPanel.add(label);

		// 添加下拉列表框
		fathers = new JComboBox<String>();
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		layout.setConstraints(fathers, c);
		this.fatherPanel.add(fathers);
	}

	/**
	 * 将各种插件组装起来,并显示
	 * 
	 * @param fathers
	 *            父任务链表
	 * @param index
	 *            父任务的位置
	 */
	protected void packPanels(LinkedList<String> fathers, int index) {
		// 首先清除之前的放置的插件
		Container con = this.getContentPane();
		con.removeAll();

		// 获得GridBagLayout布局管理器(在构造的时候设置的)
		GridBagLayout layout = (GridBagLayout) con.getLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// 总是要添加的任务内容插件
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 0.5;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(infoPanel, c);
		add(infoPanel);

		// 日任务才有的时间插件
		c.gridy = GridBagConstraints.RELATIVE;
		if (task.getClass() == DayTask.class) {
			layout.setConstraints(timePanel, c);
			add(timePanel);
		}

		// 年任务才没有的父任务插件
		if (task.getClass() != YearTask.class) {
			this.fathers.removeAllItems();
			this.fathers.addItem("<无>");
			for (String item : fathers)
				this.fathers.addItem(item);
			this.fathers.setSelectedIndex(index);// 设置选中的父任务
			layout.setConstraints(fatherPanel, c);
			add(fatherPanel);
		}

		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(ok, c);
		add(ok);
		this.getRootPane().setDefaultButton(ok);
		pack();
		this.setVisible(true);
	}

	/**
	 * 显示添加任务对话框
	 * 
	 * @param cla
	 *            要添加的任务的类型的Class对象
	 * @param fathers
	 *            此任务可继承的父任务
	 * @return 如果返回false表示这次对话被取消了,内容不可信
	 */
	public boolean showAddDialog(Class<? extends Task> cla,
			LinkedList<String> fathers) {
		this.setTitle("请输入新任务的信息");
		canceled = false;

		if (cla == DayTask.class)
			task = new DayTask("NULL", 0L, null);
		else if (cla == WeekTask.class)
			task = new WeekTask("NULL", null);
		else if (cla == MonthTask.class)
			task = new MonthTask("NULL", null);
		else if (cla == YearTask.class)
			task = new YearTask("NULL");
		else {
			JOptionPane.showMessageDialog(this, "未知的任务类型:" + cla, "程序出错了",
					JOptionPane.ERROR_MESSAGE);
			canceled = true;
			return false;
		}

		packPanels(fathers, 0);// 0是"<无>"
		return !canceled;
	}

	/**
	 * 显示修改任务对话框
	 * 
	 * @param task
	 *            要修改的任务
	 * @param fathers
	 *            此任务可继承的父任务
	 * @return 如果返回false表示这次对话被取消了,内容不可信
	 */
	public boolean showEditDialog(Task task, LinkedList<String> fathers) {
		this.setTitle("请修改此任务的内容");
		canceled = false;
		this.modifyInfo = task.info;
		try {
			this.task = (Task) task.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// 将之前的信息显示出来,用户只改需要修改的部分
		infoText.setText(task.info);// 原来的任务内容
		long need = task.needTime / 1000L / 60L;
		int minute = (int) (need % 60);
		int hour = (int) (need / 60);
		hourText.setText(Integer.toString(hour));// 原来的小时
		minuteText.setText(Integer.toString(minute));// 原来的分

		packPanels(fathers, fathers.indexOf(task.father) + 1);// +1是因为多了一项"<无>"
		return !canceled;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.canceled = false;

		task.info = infoText.getText();
		task.needTime = Integer.parseInt(hourText.getText()) * 3600 * 1000
				+ Integer.parseInt(minuteText.getText()) * 60 * 1000;
		task.father = this.fathers.getSelectedIndex() == 0 ? null
				: (String) this.fathers.getSelectedItem();
		if (task.getClass() != DayTask.class || task.needTime > 0L) // 所需时间不为0
			;
		else {
			this.canceled = true;
			JOptionPane.showMessageDialog(this, "所需时间必须大于0", "输入错误",
					JOptionPane.WARNING_MESSAGE);
		}

		// 如果不是天任务,清零所需时间
		task.needTime = task.getClass() == DayTask.class ? task.needTime : 0L;
		if (task.info.equals(TopTaskModel.TOTAL)) {
			canceled = true;
			JOptionPane.showMessageDialog(this, "\"" + TopTaskModel.TOTAL
					+ "\"" + "为保留字不能作为任务内容", "输入错误",
					JOptionPane.WARNING_MESSAGE);
		}

		this.setVisible(canceled);
	}
}
