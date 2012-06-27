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
	 * �������ĵ�������
	 */
	protected TaskDialog dialog;
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

	public TaskTable(Today today, Window top, UpdateTable updater) {
		super(new TaskModel(today, updater));

		model = (TaskModel) this.getModel();
		this.updater = updater;

		dialog = new TaskDialog(top);

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

		// delete��ɾ��
		// ����ÿ��ֻ��ѡ��һ��
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					TaskTable.this.delSelectedRow();
				// ˢ����ʾ
				model.showTasks(!check.isSelected());
				TaskTable.this.updater.updateTaskShow();
			}
		});
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

	/**
	 * ɾ��ѡ����
	 */
	protected void delSelectedRow() {
		if (this.getSelectedRow() < 0)// û���б�ѡ���򷵻�
			return;
		String info = (String) this.getValueAt(this.getSelectedRow(), 1);
		if (!today.day.isTaskEditable(info)) {// ������ɾ���򷵻�
			JOptionPane.showMessageDialog(this, "����ɵ�����������������������ɾ��", "ɾ�����ܾ�",
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
		if (cmd.equals("���������")) {
			if (!dialog.showAddDialog(DayTask.class, today.day.getFathers()))// �Ի���ȡ����,���ݲ�����
				return;
			DayTask newtask = (DayTask) dialog.task;
			today.day.add(newtask);
		} else if (cmd.equals("�޸�����")) {
			if (this.getSelectedRow() < 0)// û���б�ѡ���򷵻�
				return;
			DayTask origin = today.day.get(this.getValueAt(
					this.getSelectedRow(), 1));
			// ����������޸��򷵻�
			if (!today.day.isTaskEditable(origin.info)) {
				JOptionPane.showMessageDialog(this, "����ɵ������������������������޸�",
						"�޸ı��ܾ�", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!dialog.showEditDialog(origin, today.day.getFathers()))// �Ի���ȡ����,���ݲ�����
				return;
			today.day.modify(dialog.modifyInfo, (DayTask) dialog.task);

			// ����޸ĵ��ǵ�ǰ������,�����cur
			if (today.isWorking() && today.cur.equals(dialog.modifyInfo))
				today.cur = dialog.task.info;
		} else if (cmd.equals("ɾ������")) {
			this.delSelectedRow();
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
