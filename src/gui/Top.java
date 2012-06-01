package gui;

import inter.UpdateTable;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import data.Today;

import static gui.FormatTime.*;

public class Top extends JFrame implements ActionListener, UpdateTable {
	private static final long serialVersionUID = 1L;

	/**
	 * ������ǰ������������
	 */
	protected Today today;
	/**
	 * ��ʾ������Ϣ
	 */
	protected JLabel info;
	/**
	 * ����ǩ�ṩ���Ҽ��˵�
	 */
	protected JPopupMenu menu;
	/**
	 * �ƶ���ʼʱ���õĵ�
	 */
	protected Point origin;
	/**
	 * ��ʾ������
	 */
	protected TaskTable table;

	public Top() {
		super("�����½��ձ�");

		// ������������
		today = new Today();

		// ������ʾ���
		info = new JLabel("");
		updateLabel();
		buildMenu();
		table = new TaskTable(today, this, this);

		// ��ӱ�ǩ�ĵ���¼�,���һ�θ���һ��
		info.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				origin = e.getPoint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				updateLabel();
				pack();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					menu.show(e.getComponent(), e.getX(), e.getY());
			}

		});

		info.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = Top.this.getLocation();
				Top.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
						- origin.y);
			}

		});

		add(info, "North");
		// add(new JScrollPane(table), "Center");
		add(table, "Center");

		this.setUndecorated(true);
		this.setLocation(200, 200);
		pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * ���±�ǩ����ʾ
	 */
	protected void updateLabel() {
		String text = "<html>";
		String start = "��������"
				+ HMS(new Date().getTime() - today.getStartTime());
		String total = "����������";
		String complete = "���������";
		text += start + "<br>" + total + "<br>" + complete;
		if (today.isWorking()) {
			text += "<br>��ǰ��������" + HMS(today.getCurUsed()) + "</html>";
		}
		info.setText(text);
	}

	/**
	 * ������ǩ���Ҽ��˵�
	 */
	public void buildMenu() {
		menu = new JPopupMenu();

		String[] cmds = { "�˳�", "���", "һ��", "��С" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("�˳�")) {
			this.dispose();
		} else if (cmd.equals("���")) {
			this.setVisiblePlace(false, true);
		} else if (cmd.equals("һ��")) {
			this.setVisiblePlace(true, true);
		} else if (cmd.equals("��С")) {
			this.setVisiblePlace(true, false);
		}
	}

	/**
	 * ������ʾģʽ
	 * 
	 * @param undecorated
	 *            �ޱ߿�?
	 * @param tableVisible
	 *            �������Ƿ�ɼ�?
	 */
	protected void setVisiblePlace(boolean undecorated, boolean tableVisible) {
		this.dispose();
		this.setUndecorated(undecorated);
		if (tableVisible)
			this.addTable();
		else
			this.removeTable();
		this.updateTaskShow();
		this.setVisible(true);
	}

	/**
	 * ���table������еĻ�,�Ƴ�table
	 */
	protected void removeTable() {
		if (this.isAncestorOf(table))
			this.remove(table);
	}

	/**
	 * ���table��������еĻ�,����table
	 */
	protected void addTable() {
		if (!this.isAncestorOf(table))
			this.add(table, "Center");
	}

	/**
	 * ��ñ��Ŀǰ��ѵĴ�С
	 * 
	 * @return
	 */
	protected Dimension getPreferedTableSize() {
		Dimension d = table.getSize();
		d.height = table.getCellRect(0, 0, true).height * table.getRowCount();
		return d;
	}

	@Override
	public void updateTaskShow() {
		this.updateLabel();
		table.setPreferredSize(getPreferedTableSize());
		pack();
	}

	public static void main(String[] args) {
		Top t = new Top();
		t.setVisible(true);
	}
}
