package gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TaskDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * ��д�������ݵ�text
	 */
	protected JTextField infoText;
	/**
	 * ��дԤ����Ҫʱ���Сʱ����
	 */
	protected JTextField hourText;
	/**
	 * ��дԤ����Ҫʱ��ķ��Ӳ���
	 */
	protected JTextField minuteText;

	/**
	 * ���������
	 */
	protected String info;
	/**
	 * ����ʱ��(��λ:����)
	 */
	protected long needTime;

	public TaskDialog(Frame top) {
		super(top, "���������������Ϣ", true);

		// ���ò��ֹ�����
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		// ������������ı���
		JLabel label = new JLabel("��������:");
		layout.setConstraints(label, c);
		add(label);
		infoText = new JTextField("��������������", 20);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		layout.setConstraints(infoText, c);
		add(infoText);

		// �������ʱ���ǩ
		label = new JLabel("����ʱ��:");
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		add(label);

		// ���Сʱ�����
		hourText = new JTextField("0", 2);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(hourText, c);
		add(hourText);
		label = new JLabel("Сʱ");
		layout.setConstraints(label, c);
		add(label);

		// ��ӷ��������
		minuteText = new JTextField("3", 2);
		layout.setConstraints(minuteText, c);
		add(minuteText);
		label = new JLabel("����");
		layout.setConstraints(label, c);
		add(label);

		// ���ȷ����ť
		JButton ok = new JButton("ȷ��");
		ok.addActionListener(this);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 5;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(ok, c);
		add(ok);

		pack();
	}

	/**
	 * ��ʾ������Ի���<br>
	 * �����޸�����ʱ
	 * 
	 * @param info
	 *            ��������
	 * @param hour
	 *            ����ʱ���Сʱ����
	 * @param minute
	 *            ����ʱ��ķ��Ӳ���
	 */
	public void showup(String info, int hour, int minute) {
		infoText.setText(info);
		hourText.setText(Integer.toString(hour));
		minuteText.setText(Integer.toString(minute));
		this.setVisible(true);
	}

	/**
	 * ��ʾ�Ի�����޲����汾<br>
	 * �����½�����ʱ
	 */
	public void showup() {
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.info = infoText.getText();
		this.needTime = Integer.parseInt(hourText.getText()) * 3600 * 1000
				+ Integer.parseInt(minuteText.getText()) * 60 * 1000;
		this.dispose();
	}
}
