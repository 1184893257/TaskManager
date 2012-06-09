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
	 * 填写任务内容的text
	 */
	protected JTextField infoText;
	/**
	 * 填写预计需要时间的小时部分
	 */
	protected JTextField hourText;
	/**
	 * 填写预计需要时间的分钟部分
	 */
	protected JTextField minuteText;

	/**
	 * 任务的内容
	 */
	protected String info;
	/**
	 * 所需时间(单位:毫秒)
	 */
	protected long needTime;

	public TaskDialog(Frame top) {
		super(top, "请输入新任务的信息", true);

		// 设置布局管理器
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		// 添加任务内容文本框
		JLabel label = new JLabel("任务内容:");
		layout.setConstraints(label, c);
		add(label);
		infoText = new JTextField("请输入任务内容", 20);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		layout.setConstraints(infoText, c);
		add(infoText);

		// 添加所需时间标签
		label = new JLabel("所需时间:");
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		add(label);

		// 添加小时输入框
		hourText = new JTextField("0", 2);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(hourText, c);
		add(hourText);
		label = new JLabel("小时");
		layout.setConstraints(label, c);
		add(label);

		// 添加分钟输入框
		minuteText = new JTextField("3", 2);
		layout.setConstraints(minuteText, c);
		add(minuteText);
		label = new JLabel("分钟");
		layout.setConstraints(label, c);
		add(label);

		// 添加确定按钮
		JButton ok = new JButton("确定");
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
	 * 显示出任务对话框<br>
	 * 用于修改任务时
	 * 
	 * @param info
	 *            任务名称
	 * @param hour
	 *            所需时间的小时部分
	 * @param minute
	 *            所需时间的分钟部分
	 */
	public void showup(String info, int hour, int minute) {
		infoText.setText(info);
		hourText.setText(Integer.toString(hour));
		minuteText.setText(Integer.toString(minute));
		this.setVisible(true);
	}

	/**
	 * 显示对话框的无参数版本<br>
	 * 用于新建任务时
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
