package gui.comps;

import java.awt.*;
import javax.swing.*;

/**
 * 获取时间的组件
 * 
 * @author lqy
 * 
 */
public class TimePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * 时间的小时部分
	 */
	protected JTextField hourText;
	/**
	 * 时间的分钟部分
	 */
	protected JTextField minuteText;

	public TimePanel() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// 添加小时输入框
		hourText = new JTextField("0", 2);
		c.gridy = 0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(hourText, c);
		this.add(hourText);

		JLabel label = new JLabel("小时");
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		this.add(label);

		// 添加分钟输入框
		minuteText = new JTextField("3", 2);
		c.weightx = 0.5;
		layout.setConstraints(minuteText, c);
		this.add(minuteText);

		label = new JLabel("分钟");
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		this.add(label);
	}

	/**
	 * 设置 小时\分
	 * 
	 * @param time
	 *            时间:毫秒
	 */
	public void setTime(long time) {
		time = time / (1000 * 60);
		int minute = (int) (time % 60);
		int hour = (int) (time / 60);
		hourText.setText(Integer.toString(hour));// 原来的小时
		minuteText.setText(Integer.toString(minute));// 原来的分
	}

	/**
	 * 获得小时\分加起来的时间
	 * 
	 * @return 时间:毫秒
	 */
	public long getTime() {
		return Integer.parseInt(hourText.getText()) * (3600 * 1000L)
				+ Integer.parseInt(minuteText.getText()) * (60 * 1000L);
	}

}
