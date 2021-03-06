package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;

import inter.Updater;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import data.Cmps;
import data.TopTaskModel;
import data.task.Task;
import data.tasks.TaskMap;

/**
 * 通用任务表格组件
 * 
 * @author lqy
 * 
 * @param <E>
 *            此表格中单个任务的类型
 */
public class TopTaskTable<E extends Task> extends JTable implements
		ActionListener {
	private static final long serialVersionUID = 1L;

	// 以下几个是构造的时候传进来的
	/**
	 * 是E的Class对象,没法用E来获得,只好用参数传进来
	 */
	protected Class<E> taskClass;
	/**
	 * 单个任务编辑对话框,在添加修改任务的时候弹出
	 */
	protected TaskDialog dialog;
	/**
	 * 任务内容所在列
	 */
	protected int contentCol;
	/**
	 * 允许任务内容突出显示不?<br>
	 * 只有今天才能将任务内容变红加粗
	 */
	protected boolean isTodayTable;
	/**
	 * 主界面提供的刷新接口<br>
	 * 在添加\删除操作后界面应该按新的情况重新布局
	 */
	protected Updater updater;
	/**
	 * 此表格的上一级表格组件<br>
	 * 当此表格的内容改动后用于同步上级的刷新,这是一条链,所有的表格都会自动刷新了
	 */
	protected TopTaskTable<? extends Task> father;
	/**
	 * 此表格的数据模型<br>
	 * 在事件处理中要获得相关数据还得靠它
	 */
	protected TopTaskModel<E> model;

	// 以下几个是自己构造的
	/**
	 * 显示已完成?
	 */
	protected JCheckBoxMenuItem check;
	/**
	 * 右键弹出菜单,选中行和右键单击行相同时可删除
	 */
	protected JPopupMenu menu;
	/**
	 * 普通任务的字体
	 */
	protected final Font normalFont;
	/**
	 * 已完成任务的字体
	 */
	protected final Font finishedFont;
	/**
	 * 正在执行的任务的字体
	 */
	protected final Font runningFont;
	/**
	 * 任务内容列的渲染器
	 */
	protected MyRender render;

	// 排序相关的4个菜单项
	protected JCheckBoxMenuItem reversed;
	protected JRadioButtonMenuItem nameSort;
	protected JRadioButtonMenuItem needSort;
	protected JRadioButtonMenuItem lastSort;
	protected ButtonGroup sortGroup;

	/**
	 * 构造任务表格
	 * 
	 * @param taskClass
	 *            此任务表格关注的任务的类型
	 * @param dialog
	 *            单个任务编辑对话框
	 * @param contentCol
	 *            任务内容显示的列
	 * @param canHighlight
	 *            允许任务内容突出显示不
	 * @param updater
	 *            主界面提供的刷新接口
	 * @param father
	 *            此表格的上一级表格组件
	 * @param model
	 *            此表格的数据模型
	 */
	public TopTaskTable(Class<E> taskClass, TaskDialog dialog, int contentCol,
			boolean canHighlight, Updater updater,
			TopTaskTable<? extends Task> father, TopTaskModel<E> model) {
		super(model);
		this.taskClass = taskClass;
		this.dialog = dialog;
		this.contentCol = contentCol;
		this.isTodayTable = canHighlight;
		this.updater = updater;
		this.father = father;
		this.model = model;

		// 添加右键菜单
		buildMenu();
		// 在鼠标release后弹出菜单
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

		});

		// 表格监听delete按删除一个任务
		// 一次只能选一行
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					remove();
					TopTaskTable.this.updateFromMem();
					TopTaskTable.this.updater.update();
				}
			}

		});

		// 设置3种字体
		final int size = 12;
		final String font = "微软雅黑";
		this.normalFont = new Font(font, 0, size);
		this.finishedFont = new Font(font, 0, size);
		this.runningFont = new Font(font, Font.BOLD, size);

		// 渲染任务内容行
		render = new MyRender();
		this.getColumnModel().getColumn(contentCol).setCellRenderer(render);
	}

	/**
	 * info是不是当前正在执行的任务<br>
	 * 只有此类的子类TodayTable才有可能返回true
	 * 
	 * @param info
	 * @return
	 */
	protected boolean isCur(String info) {
		String cur = model.getCur();
		return this.isTodayTable && cur != null && cur.equals(info);
	}

	/**
	 * 建立右键菜单
	 */
	protected void buildMenu() {
		menu = new JPopupMenu();

		// 添加了可选菜单项
		check = new JCheckBoxMenuItem("隐藏已完成");
		check.addActionListener(this);
		menu.add(check);
		menu.addSeparator();// 添加一个分隔符

		// 添加其他菜单
		String[] cmds = { "添加新任务", "修改任务", "删除任务" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
		}

		menu.addSeparator();// 添加一个分隔符

		// 添加逆序可选菜单
		reversed = new JCheckBoxMenuItem("逆序");
		reversed.setSelected(false);// 默认正序
		reversed.addActionListener(this);
		menu.add(reversed);

		// 添加3个排序选项
		String[] sorts = { "任务名排序", "所需时间排序", "所用时间排序" };
		sortGroup = new ButtonGroup();
		JRadioButtonMenuItem[] sorters = new JRadioButtonMenuItem[sorts.length];
		for (i = 0; i < sorts.length; ++i) {
			sorters[i] = new JRadioButtonMenuItem(sorts[i]);
			sorters[i].addActionListener(this);
			sorters[i].getModel().setActionCommand(sorts[i]);
			sortGroup.add(sorters[i]);
			menu.add(sorters[i]);
		}
		nameSort = sorters[0];
		needSort = sorters[1];
		lastSort = sorters[2];
		nameSort.setSelected(true);// 默认按任务名排序
	}

	/**
	 * 内存中的任务集合可能已经变更,重新读取任务集合刷新显示<br>
	 * 此表格的父表格可能也有改变,递归的刷新
	 */
	public void updateFromMem() {
		model.showTasks(!check.isSelected());
		if (father != null)
			father.updateFromMem();
	}

	/**
	 * 文件中的任务集合可能已经变更,从文件读取任务集合刷新显示<br>
	 * 此表格的父表格可能也有改变,递归的刷新
	 */
	public void updateFromFile() {
		TaskMap<E, ? extends Task> tasks = model.getTasks();
		tasks.readTasks();
		model.showTasks(!check.isSelected());
		if (father != null)
			father.updateFromFile();
	}

	/**
	 * 本表格所代表的任务集合可能已经变更,从内存更新本表格<br>
	 * <p>
	 * 不级联更新上层表格,在下拉按钮的事件中只需更新一个表格,<br>
	 * 就会用到这个方法
	 */
	public void updateJustMe() {
		model.showTasks(!check.isSelected());
	}

	/**
	 * 添加任务的具体处理
	 */
	protected void add() {
		TaskMap<E, ? extends Task> tasks = model.getTasks();
		if (!dialog.showAddDialog(taskClass, tasks.getFathers()))// 对话被取消了,内容不可信
			return;
		@SuppressWarnings("unchecked")
		E newtask = (E) dialog.task;
		tasks.add(newtask);
	}

	/**
	 * 删除任务的具体处理
	 */
	protected void remove() {
		do {
			if (this.getSelectedRow() < 0)// 没有行被选中则返回
				break;

			TaskMap<E, ? extends Task> tasks = model.getTasks();

			// 获得任务内容,如果是TOTAL则提示错误并返回
			String info = (String) this.getValueAt(this.getSelectedRow(),
					contentCol);
			if (info.equals(TopTaskModel.TOTAL)) {
				JOptionPane.showMessageDialog(this, "\"总计\"行不能删除", "删除被拒绝",
						JOptionPane.ERROR_MESSAGE);
				break;
			}

			// 不允许删除当前正在执行的任务
			if (this.isCur(info)) {
				JOptionPane.showMessageDialog(this, "当前正在执行的行不能删除", "删除被拒绝",
						JOptionPane.ERROR_MESSAGE);
				break;
			}

			if (!tasks.isTaskEditable(info)) {// 不允许删除则返回
				JOptionPane.showMessageDialog(this, "已完成的任务或已有子任务的任务不能删除",
						"删除被拒绝", JOptionPane.ERROR_MESSAGE);
				break;
			}

			tasks.remove(info);
		} while (false);
	}

	/**
	 * 修改任务的具体处理
	 */
	@SuppressWarnings("unchecked")
	protected void modify() {
		do {
			if (this.getSelectedRow() < 0)// 没有行被选中则返回
				break;

			TaskMap<E, ? extends Task> tasks = model.getTasks();

			// 获得任务内容,如果是TOTAL则提示错误并返回
			String info = (String) this.getValueAt(this.getSelectedRow(),
					contentCol);
			if (info.equals(TopTaskModel.TOTAL)) {
				JOptionPane.showMessageDialog(this, "\"总计\"行不能修改", "修改被拒绝",
						JOptionPane.ERROR_MESSAGE);
				break;
			}

			// 不允许修改当前正在执行的任务
			if (this.isCur(info)) {
				JOptionPane.showMessageDialog(this, "当前正在执行的行不能修改", "修改被拒绝",
						JOptionPane.ERROR_MESSAGE);
				break;
			}

			E origin = tasks.get(info);
			// 如果不允许修改则返回
			if (!tasks.isTaskEditable(origin.info)) {
				JOptionPane.showMessageDialog(this, "已完成的任务或已有子任务的任务不能修改",
						"修改被拒绝", JOptionPane.ERROR_MESSAGE);
				break;
			}

			if (!dialog.showEditDialog(origin, tasks.getFathers()))// 对话被取消了,内容不可信
				break;
			tasks.modify(dialog.modifyInfo, (E) dialog.task);
		} while (false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		// 可能更改了上层表格?改了的话就得级联更新表格,没改就只要更新本表格
		boolean fatherModified = true;

		if (cmd.equals("添加新任务"))
			add();
		else if (cmd.equals("修改任务"))
			modify();
		else if (cmd.equals("删除任务"))
			remove();
		else
			fatherModified = false;

		// 是添加/修改/删除?
		if (fatherModified) {
			this.updateFromMem();// 更新此表格以上各表格的显示
			updater.update();
			return;
		}

		// 是隐藏或重新排序
		if (cmd.equals("隐藏已完成"))
			;
		else {// 重新设置排序方式
			cmd = sortGroup.getSelection().getActionCommand();
			int pos = 0;
			if (cmd.equals("任务名排序"))
				pos = 0;
			else if (cmd.equals("所需时间排序"))
				pos = 1;
			else if (cmd.equals("所用时间排序"))
				pos = 2;
			else
				System.err.println("没有<" + cmd + ">排序方式");

			model.setCmp((Comparator<Task>) Cmps.cmps[reversed.isSelected() ? 1
					: 0][pos]);
		}
		this.updateJustMe();

		updater.update();
	}

	/**
	 * 第1列文字的单元格渲染器
	 * 
	 * @author lqy
	 * 
	 */
	protected class MyRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component ans;
			// 取得要显示的组件
			ans = super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			TaskMap<E, ? extends Task> tasks = model.getTasks();

			String cur = model.getCur();
			// 如果是"总计"行,不渲染
			if (((String) value).equals(TopTaskModel.TOTAL))
				;
			// 如果是当前任务,则加粗
			else if (isTodayTable && cur != null && cur.equals(value)) {
				ans.setFont(TopTaskTable.this.runningFont);
				ans.setForeground(Color.red);
			}
			// 如果是已完成任务,style为0,颜色为灰
			else if (tasks.get(value).finished) {
				ans.setFont(TopTaskTable.this.finishedFont);
				ans.setForeground(Color.gray);
			}
			// 普通的未完成的任务,style为0,颜色为黑
			else {
				ans.setFont(TopTaskTable.this.normalFont);
				ans.setForeground(Color.black);
			}
			return ans;
		}

	}

}
