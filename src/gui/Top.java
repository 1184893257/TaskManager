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
	 * 包含当前运行所需数据
	 */
	protected Today today;
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

	public Top() {
		super("今日事今日毕");

		// 生成运行数据
		today = new Today();

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
			text += "<br>当前任务已用" + HMS(today.getCurUsed()) + "</html>";
		}
		info.setText(text);
	}

	/**
	 * 建立标签的右键菜单
	 */
	public void buildMenu() {
		menu = new JPopupMenu();

		String[] cmds = { "退出", "最大", "一般", "最小" };
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
		if (cmd.equals("退出")) {
			this.dispose();
		} else if (cmd.equals("最大")) {
			this.setVisiblePlace(false, true);
		} else if (cmd.equals("一般")) {
			this.setVisiblePlace(true, true);
		} else if (cmd.equals("最小")) {
			this.setVisiblePlace(true, false);
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
