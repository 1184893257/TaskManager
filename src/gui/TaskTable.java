package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import data.TaskModel;
import data.Today;

/**
 * 显示任务表格的组件
 * 
 * @author lqy
 * 
 */
public class TaskTable extends JTable implements ActionListener {
	private static final long serialVersionUID = 1L;

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

	public TaskTable(Today today) {
		super(new TaskModel());

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
	}
}
