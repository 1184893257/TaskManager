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
	 * 包含当前运行所需数据
	 */
	protected Today today;
	/**
	 * 显示整体信息
	 */
	protected JLabel info;

	public Top() {
		super("今日事今日毕");

		// 生成运行数据
		today = new Today();

		// 创建显示组件
		info = new JLabel("");
		updateLabel();
		TaskTable table = new TaskTable(today);

		// 添加标签的点击事件,点击一次更新一次
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
	 * 更新标签的显示
	 */
	protected void updateLabel() {
		String text = "<html>";
		String start = "开机运行"
				+ HMS(new Date().getTime() - today.getStartTime());
		String total = "今日总任务";
		String complete = "今日已完成";
		text += start + "<br>" + total + "<br>" + complete;
		if (today.isWorking()) {
			text += "当前任务已用" + HMS(today.getCurUsed()) + "</html>";
		}
		info.setText(text);
	}

	public static void main(String[] args) {
		Top t = new Top();
		t.setVisible(true);
	}
}
