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
	 * ��ʾ�����?
	 */
	protected JCheckBoxMenuItem check;
	/**
	 * ��1�е���Ⱦ��
	 */
	protected MyRender render;
	/**
	 * ������д�С���ʱ,����updater�ػ���
	 */
	protected UpdateTable updater;
	/**
	 * �ṩ����������frame�õ����������
	 */
	protected Frame top;
	/**
	 * �������ĵ�������
	 */
	protected TaskDialog frame;
	/**
	 * ��ͨ���������
	 */
	protected final Font normalFont;
	/**
	 * ��������������
	 */
	protected final Font finishedFont;
	/**
	 * ����ִ�е����������
	 */
	protected final Font runningFont;

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
		buildMenu();

		// �����release�󵯳��˵�
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

		// ����3������
		final int size = 12;
		final String font = "΢���ź�";
		this.normalFont = new Font(font, 0, size);
		this.finishedFont = new Font(font, 0, size);
		this.runningFont = new Font(font, Font.BOLD, size);
	}

	/**
	 * �����Ҽ��˵�
	 */
	protected void buildMenu() {
		menu = new JPopupMenu();
		String[] cmds = { "���������", "���������", "�޸�����", "ɾ������" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			if (cmds[i].equals("���������")) {
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
		if (cmd.equals("���������")) {
			if (frame == null)
				frame = new TaskDialog(top);
			frame.showup();
			DayTask newtask = new DayTask(frame.info, frame.needTime, null);

			/* ���ģ�����һ��Ҫ�����񼯺����֮��,��Ϊ�����ʾ��ʱ��Ҫ�����񼯺��в��Ҷ�Ӧ���� */
			today.day.add(newtask);
		} else if (cmd.equals("�޸�����")) {
			// ���ѡ�е�Ҫ�޸ĵ�����
			DayTask origin = today.day.get(this.getValueAt(
					this.getSelectedRow(), 1));

			// ���öԻ������ʾ
			if (frame == null)
				frame = new TaskDialog(top);
			long need = origin.needTime / 1000L / 60L;
			int minute = (int) (need % 60);
			int hour = (int) (need / 60);
			frame.showup(origin.info, hour, minute);

			// �ύ�޸�
			String originInfo = origin.info;
			origin.info = frame.info;
			origin.needTime = frame.needTime;
			today.day.modify(originInfo, origin);
		} else if (cmd.equals("ɾ������")) {
			today.day
					.remove((String) this.getValueAt(this.getSelectedRow(), 1));
		}
		// ˢ����ʾ
		model.showTasks(!check.isSelected());
		updater.updateTaskShow();
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
				ans.setFont(TaskTable.this.runningFont);
				ans.setForeground(Color.red);
			}
			// ��������������,styleΪ0,��ɫΪ��
			else if (today.day.get(value).finished) {
				ans.setFont(TaskTable.this.finishedFont);
				ans.setForeground(Color.gray);
			}
			// ��ͨ��δ��ɵ�����,styleΪ0,��ɫΪ��
			else {
				ans.setFont(TaskTable.this.normalFont);
				ans.setForeground(Color.black);
			}
			return ans;
		}

	}
}
