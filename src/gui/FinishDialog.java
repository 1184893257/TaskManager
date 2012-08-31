package gui;

import gui.comps.TimePanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * 可以提供完成时间的对话框
 * 
 * @author lqy
 * 
 */
public class FinishDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * 获取时间的组件
	 */
	protected TimePanel timePanel;
	/**
	 * 是否取消这次完成任务的操作
	 */
	protected boolean canceled;
	/**
	 * 确定按钮
	 */
	protected JButton button;

	public FinishDialog(Window owner) {
		super(owner, ModalityType.DOCUMENT_MODAL);
		this.setTitle("请输入任务所用时间");

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// 放置标签
		JLabel label = new JLabel("所用时间:");
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		layout.setConstraints(label, c);
		add(label);

		// 放置时间组件
		timePanel = new TimePanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		layout.setConstraints(timePanel, c);
		add(timePanel);

		// 放置确定按钮
		button = new JButton("确定");
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
	 * 取得对话框中的所用时间
	 * 
	 * @return 时间 单位:毫秒
	 */
	public long getFinishTime() {
		return timePanel.getTime();
	}

	/**
	 * 显示输入完成任务所用时间的对话框，返回false（点击关闭对话框按钮）表示取消这次完成操作
	 * 
	 * @return 是否确定这次完成操作
	 */
	public boolean showFinishDialog() {
		canceled = false;
		this.getRootPane().setDefaultButton(button);
		this.setVisible(true);
		return !canceled;
	}

}
