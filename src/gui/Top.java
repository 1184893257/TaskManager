package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;

import data.Today;

import static gui.FormatTime.*;

public class Top extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * ������ǰ������������
	 */
	protected Today today;
	/**
	 * ��ʾ������Ϣ
	 */
	protected JLabel info;

	public Top() {
		super("�����½��ձ�");

		// ������������
		today = new Today();

		// ������ʾ���
		info = new JLabel("");
		updateLabel();
		TaskTable table = new TaskTable(today);

		// ��ӱ�ǩ�ĵ���¼�,���һ�θ���һ��
		info.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				updateLabel();
			}

		});

		add(info, "North");
		// add(new JScrollPane(table), "Center");
		add(table, "Center");

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
			text += "��ǰ��������" + HMS(today.getCurUsed()) + "</html>";
		}
		info.setText(text);
	}

	public static void main(String[] args) {
		Top t = new Top();
		t.setVisible(true);
	}
}
