package gui;

import inter.UpdateTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

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
	 * 右键弹出菜单
	 */
	protected JPopupMenu menu2;
	/**
	 * 第1列的渲染器
	 */
	protected MyRender render;
	/**
	 * 顶层窗体<br>
	 * 添加任务弹出对话框需要
	 */
	protected Frame top;
	/**
	 * 当表格有大小变更时,调用updater重绘表格
	 */
	protected UpdateTable updater;

	public TaskTable(Today today, Frame top, UpdateTable updater) {
		super(new TaskModel(today, updater));

		model = (TaskModel) this.getModel();
		this.top = top;
		this.updater = updater;

		// 表格不显示纵向分割线
		this.setShowVerticalLines(false);

		// 设置第1列的渲染器
		render = new MyRender();
		this.getColumnModel().getColumn(1).setCellRenderer(render);

		// 表格也需使用运行信息
		this.today = today;

		// 添加右键菜单
		menu = new JPopupMenu();
		menu2 = new JPopupMenu();
		String[] cmds = { "添加", "删除" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
			if (!cmds[i].equals("删除")) {
				t = new JMenuItem(cmds[i]);
				t.addActionListener(this);
				menu2.add(t);
			}
		}

		// 在鼠标release后弹出菜单
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int r1 = TaskTable.this.getSelectedRow();
					int r2 = TaskTable.this.rowAtPoint(e.getPoint());
					if (r1 == r2)
						menu.show(e.getComponent(), e.getX(), e.getY());
					else
						menu2.show(e.getComponent(), e.getX(), e.getY());
				}
			}

		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("添加")) {
			AddTask frame = new AddTask(top);
			frame.setVisible(true);
			DayTask newtask = new DayTask(frame.info, frame.needTime);

			/* 表格模型添加一定要在任务集合添加之后,因为表格显示的时候要从任务集合中查找对应任务 */
			today.tasks.add(newtask);
			model.addTask(newtask);
			updater.updateTaskShow();
		}
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
				ans.setFont(ans.getFont().deriveFont(Font.BOLD));
				ans.setForeground(Color.red);
			}
			// 如果是已完成任务,style为0,颜色为灰
			else if (today.tasks.get(value).finished) {
				ans.setFont(ans.getFont().deriveFont(0));
				ans.setForeground(Color.gray);
			}
			// 普通的未完成的任务,style为0,颜色为黑
			else {
				ans.setFont(ans.getFont().deriveFont(0));
				ans.setForeground(Color.black);
			}
			return ans;
		}

	}
}
