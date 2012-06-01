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
 * ��ʾ����������
 * 
 * @author lqy
 * 
 */
public class TaskTable extends JTable implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * ��������ģ��
	 */
	protected TaskModel model;
	/**
	 * ����ʱ����
	 */
	protected Today today;
	/**
	 * �Ҽ������˵�,ѡ���к��Ҽ���������ͬʱ��ɾ��
	 */
	protected JPopupMenu menu;
	/**
	 * �Ҽ������˵�
	 */
	protected JPopupMenu menu2;
	/**
	 * ��1�е���Ⱦ��
	 */
	protected MyRender render;
	/**
	 * ���㴰��<br>
	 * ������񵯳��Ի�����Ҫ
	 */
	protected Frame top;
	/**
	 * ������д�С���ʱ,����updater�ػ���
	 */
	protected UpdateTable updater;

	public TaskTable(Today today, Frame top, UpdateTable updater) {
		super(new TaskModel(today, updater));

		model = (TaskModel) this.getModel();
		this.top = top;
		this.updater = updater;

		// �����ʾ����ָ���
		this.setShowVerticalLines(false);

		// ���õ�1�е���Ⱦ��
		render = new MyRender();
		this.getColumnModel().getColumn(1).setCellRenderer(render);

		// ���Ҳ��ʹ��������Ϣ
		this.today = today;

		// ����Ҽ��˵�
		menu = new JPopupMenu();
		menu2 = new JPopupMenu();
		String[] cmds = { "���", "ɾ��" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
			if (!cmds[i].equals("ɾ��")) {
				t = new JMenuItem(cmds[i]);
				t.addActionListener(this);
				menu2.add(t);
			}
		}

		// �����release�󵯳��˵�
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
		if (cmd.equals("���")) {
			AddTask frame = new AddTask(top);
			frame.setVisible(true);
			DayTask newtask = new DayTask(frame.info, frame.needTime);

			/* ���ģ�����һ��Ҫ�����񼯺����֮��,��Ϊ�����ʾ��ʱ��Ҫ�����񼯺��в��Ҷ�Ӧ���� */
			today.tasks.add(newtask);
			model.addTask(newtask);
			updater.updateTaskShow();
		}
	}

	/**
	 * ��1�����ֵĵ�Ԫ����Ⱦ��
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
			// ȡ��Ҫ��ʾ�����
			ans = super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			// ����ǵ�ǰ����,��Ӵ�
			if (today.cur != null && today.cur.equals(value)) {
				ans.setFont(ans.getFont().deriveFont(Font.BOLD));
				ans.setForeground(Color.red);
			}
			// ��������������,styleΪ0,��ɫΪ��
			else if (today.tasks.get(value).finished) {
				ans.setFont(ans.getFont().deriveFont(0));
				ans.setForeground(Color.gray);
			}
			// ��ͨ��δ��ɵ�����,styleΪ0,��ɫΪ��
			else {
				ans.setFont(ans.getFont().deriveFont(0));
				ans.setForeground(Color.black);
			}
			return ans;
		}

	}
}
