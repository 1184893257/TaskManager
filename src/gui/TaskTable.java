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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
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
	 * 提供给弹出窗体frame用的主窗体对象
	 */
	protected Frame top;
	/**
	 * 添加任务的弹出窗体
	 */
	protected TaskDialog frame;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("添加新任务")) {
			if (frame == null)
				frame = new TaskDialog(top);
			frame.showup();
			DayTask newtask = new DayTask(frame.info, frame.needTime, null);

			/* 表格模型添加一定要在任务集合添加之后,因为表格显示的时候要从任务集合中查找对应任务 */
			today.day.add(newtask);
		} else if (cmd.equals("修改任务")) {
			// 获得选中的要修改的任务
			DayTask origin = today.day.get(this.getValueAt(
					this.getSelectedRow(), 1));

			// 设置对话框的显示
			if (frame == null)
				frame = new TaskDialog(top);
			long need = origin.needTime / 1000L / 60L;
			int minute = (int) (need % 60);
			int hour = (int) (need / 60);
			frame.showup(origin.info, hour, minute);

			// 提交修改
			String originInfo = origin.info;
			origin.info = frame.info;
			origin.needTime = frame.needTime;
			today.day.modify(originInfo, origin);
		} else if (cmd.equals("删除任务")) {
			today.day
					.remove((String) this.getValueAt(this.getSelectedRow(), 1));
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
