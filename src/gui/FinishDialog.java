package gui;

import gui.comps.TimePanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * �����ṩ���ʱ��ĶԻ���
 * 
 * @author lqy
 * 
 */
public class FinishDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * ��ȡʱ������
	 */
	protected TimePanel timePanel;
	/**
	 * �Ƿ�ȡ������������Ĳ���
	 */
	protected boolean canceled;
	/**
	 * ȷ����ť
	 */
	protected JButton button;

	public FinishDialog(Window owner) {
		super(owner, ModalityType.DOCUMENT_MODAL);
		this.setTitle("��������������ʱ��");

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// ���ñ�ǩ
		JLabel label = new JLabel("����ʱ��:");
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		layout.setConstraints(label, c);
		add(label);

		// ����ʱ�����
		timePanel = new TimePanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		layout.setConstraints(timePanel, c);
		add(timePanel);

		// ����ȷ����ť
		button = new JButton("ȷ��");
		c.gridy = GridBagConstraints.RELATIVE;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(button, c);
		add(button);
		button.addActionListener(this);

		pack();
		StaticMethod.locOnCenter(this);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
				FinishDialog.this.setVisible(false);
			}

		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	/**
	 * ȡ�öԻ����е�����ʱ��
	 * 
	 * @return ʱ�� ��λ:����
	 */
	public long getFinishTime() {
		return timePanel.getTime();
	}

	/**
	 * ��ʾ���������������ʱ��ĶԻ��򣬷���false������رնԻ���ť����ʾȡ�������ɲ���
	 * 
	 * @return �Ƿ�ȷ�������ɲ���
	 */
	public boolean showFinishDialog() {
		canceled = false;
		this.getRootPane().setDefaultButton(button);
		this.setVisible(true);
		return !canceled;
	}

}
