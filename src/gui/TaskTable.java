package gui;

import inter.UpdateTable;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import data.TaskModel;
import data.Today;
import data.task.DayTask;

/**
 * 显示任务表格的组件
 * 
 * @author lqy
 * 
 */
public class TaskTable extends JTable implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * 表格的数据模型
	 */
	protected TaskModel model;
	/**
	 * 运行时数据
	 */
	protected Today today;
	/**
	 * 右键弹出菜单,选中行和右键单击行相同时可删除
	 */
	protected JPopupMenu menu;
	/**
	 * 显示已完成?
	 */
	protected JCheckBoxMenuItem check;
	/**
	 * 第1列的渲染器
	 */
	protected MyRender render;
	/**
	 * 当表格有大小变更时,调用updater重绘表格
	 */
	protected UpdateTable updater;
	/**
	 * 添加任务的弹出窗体
	 */
	protected TaskDialog dialog;
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

	public TaskTable(Today today, Window top, UpdateTable updater) {
		super(new TaskModel(today, updater));

		model = (TaskModel) this.getModel();
		this.updater = updater;

		dialog = new TaskDialog(top);

		// 表格不显示纵向分割线
		this.setShowVerticalLines(false);

		// 设置第1列的渲染器
		render = new MyRender();
		this.getColumnModel().getColumn(1).setCellRenderer(render);

		// 表格也需使用运行信息
		this.today = today;

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

		});

		TableColumnModel colModel = this.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(300);
		colModel.getColumn(2).setPreferredWidth(100);
		colModel.getColumn(3).setPreferredWidth(20);

		// 设置3种字体
		final int size = 12;
		final String font = "微软雅黑";
		this.normalFont = new Font(font, 0, size);
		this.finishedFont = new Font(font, 0, size);
		this.runningFont = new Font(font, Font.BOLD, size);

		// delete键删除
		// 设置每次只能选中一行
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					TaskTable.this.delSelectedRow();
				// 刷新显示
				model.showTasks(!check.isSelected());
				TaskTable.this.updater.updateTaskShow();
			}
		});
	}

	/**
	 * 建立右键菜单
	 */
	protected void buildMenu() {
		menu = new JPopupMenu();
		String[] cmds = { "隐藏已完成", "添加新任务", "修改任务", "删除任务" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			if (cmds[i].equals("隐藏已完成")) {
				check = new JCheckBoxMenuItem(cmds[i]);
				check.addActionListener(this);
				menu.add(check);
				menu.addSeparator();
				continue;
			}
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
		}
	}

	/**
	 * 删除选中行
	 */
	protected void delSelectedRow() {
		if (this.getSelectedRow() < 0)// 没有行被选中则返回
			return;
		String info = (String) this.getValueAt(this.getSelectedRow(), 1);
		if (!today.day.isTaskEditable(info)) {// 不允许删除则返回
			JOptionPane.showMessageDialog(this, "已完成的任务或已有子任务的任务不能删除", "删除被拒绝",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		today.day.remove(info);
		if (today.isWorking() && today.cur.equals(info))
			today.cur = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("添加新任务")) {
			if (!dialog.showAddDialog(DayTask.class, today.day.getFathers()))// 对话被取消了,内容不可信
				return;
			DayTask newtask = (DayTask) dialog.task;
			today.day.add(newtask);
		} else if (cmd.equals("修改任务")) {
			if (this.getSelectedRow() < 0)// 没有行被选中则返回
				return;
			DayTask origin = today.day.get(this.getValueAt(
					this.getSelectedRow(), 1));
			// 如果不允许修改则返回
			if (!today.day.isTaskEditable(origin.info)) {
				JOptionPane.showMessageDialog(this, "已完成的任务或已有子任务的任务不能修改",
						"修改被拒绝", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!dialog.showEditDialog(origin, today.day.getFathers()))// 对话被取消了,内容不可信
				return;
			today.day.modify(dialog.modifyInfo, (DayTask) dialog.task);

			// 如果修改的是当前的任务,则更新cur
			if (today.isWorking() && today.cur.equals(dialog.modifyInfo))
				today.cur = dialog.task.info;
		} else if (cmd.equals("删除任务")) {
			this.delSelectedRow();
		}
		// 刷新显示
		model.showTasks(!check.isSelected());
		updater.updateTaskShow();
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

			// 如果是当前任务,则加粗
			if (today.cur != null && today.cur.equals(value)) {
				ans.setFont(TaskTable.this.runningFont);
				ans.setForeground(Color.red);
			}
			// 如果是已完成任务,style为0,颜色为灰
			else if (today.day.get(value).finished) {
				ans.setFont(TaskTable.this.finishedFont);
				ans.setForeground(Color.gray);
			}
			// 普通的未完成的任务,style为0,颜色为黑
			else {
				ans.setFont(TaskTable.this.normalFont);
				ans.setForeground(Color.black);
			}
			return ans;
		}

	}
}
