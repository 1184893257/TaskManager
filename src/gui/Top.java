package gui;

import inter.UpdateTable;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import data.Today;

import static gui.FormatTime.*;

public class Top extends JFrame implements ActionListener, UpdateTable {
	private static final long serialVersionUID = 1L;

	/**
	 * 包含当前运行所需数据
	 */
	protected Today today;
	/**
	 * 今天的年/月/日 星期
	 */
	protected String date;
	/**
	 * 显示整体信息
	 */
	protected JLabel info;
	/**
	 * 给标签提供的右键菜单
	 */
	protected JPopupMenu menu;
	/**
	 * 移动开始时设置的点
	 */
	protected Point origin;
	/**
	 * 显示任务表格
	 */
	protected TaskTable table;
	/**
	 * 始终置顶可选框
	 */
	protected JCheckBoxMenuItem alwaysTop;

	public Top() {
		super("今日事今日毕");

		// 生成运行数据
		today = new Today();

		// 生成日期
		String[] week = { "日", "一", "二", "三", "四", "五", "六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		date = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1)
				+ "/" + cal.get(Calendar.DATE) + " "
				+ week[cal.get(Calendar.DAY_OF_WEEK) - 1];

		// 创建显示组件
		info = new JLabel("");
		updateLabel();
		buildMenu();
		table = new TaskTable(today, this, this);

		// 添加标签的点击事件,点击一次更新一次
		info.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				origin = e.getPoint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				updateTaskShow();
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
		add(table, "Center");

		this.setUndecorated(true);
		this.setLocation(200, 200);
		pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * 更新标签的显示
	 */
	protected void updateLabel() {
		Date now = new Date();
		String text = "<html>" + date + "<br>";
		String start = "开机运行" + HMS(now.getTime() - today.getStartTime());
		String total = "今日总任务" + HMS(today.day.getTotal());
		String complete = "今日已完成" + HMS(today.day.getFinished());
		String vacancy = "空闲了"
				+ HMS(today.isWorking() ? today.vacancy : today.vacancy
						+ now.getTime() - today.startLazy.getTime());
		text += start + "<br>" + total + "<br>" + complete + "<br>" + vacancy;
		if (today.isWorking()) {
			text += "<br>当前任务已用" + HMS(today.getCurUsed());
		}
		info.setText(text);
	}

	/**
	 * 建立标签的右键菜单
	 */
	protected void buildMenu() {
		menu = new JPopupMenu();

		String[] cmds = { "置顶", "退出", "最大", "一般", "最小" };
		int i;
		JMenuItem t;
		for (i = 0; i < cmds.length; ++i) {
			if (cmds[i].equals("置顶")) {
				alwaysTop = new JCheckBoxMenuItem(cmds[i]);
				alwaysTop.addActionListener(this);
				menu.add(alwaysTop);
				continue;
			}
			t = new JMenuItem(cmds[i]);
			t.addActionListener(this);
			menu.add(t);
			if (i == 1)
				menu.addSeparator();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("退出")) {
			this.dispose();
		} else if (cmd.equals("最大")) {
			this.setVisiblePlace(false, true);
		} else if (cmd.equals("一般")) {
			this.setVisiblePlace(true, true);
		} else if (cmd.equals("最小")) {
			this.setVisiblePlace(true, false);
		} else if (cmd.equals("置顶")) {
			this.setAlwaysOnTop(alwaysTop.isSelected());
		}
	}

	/**
	 * 设置显示模式
	 * 
	 * @param undecorated
	 *            无边框?
	 * @param tableVisible
	 *            任务表格是否可见?
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
	 * 如果table在面板中的话,移除table
	 */
	protected void removeTable() {
		if (this.isAncestorOf(table))
			this.remove(table);
	}

	/**
	 * 如果table不在面板中的话,加入table
	 */
	protected void addTable() {
		if (!this.isAncestorOf(table))
			this.add(table, "Center");
	}

	/**
	 * 获得表格目前最佳的大小
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
