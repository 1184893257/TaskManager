package gui;

import java.awt.*;

public class StaticMethod {
	/**
	 * 将时间间隔passed(单位:毫秒)转化为HH:MM:SS的格式
	 * 
	 * @param passed
	 *            时间间隔(单位:毫秒)
	 * @return HH:MM:SS格式的时间
	 */
	public static String HMS(long passed) {
		int h, m, s;
		passed /= 1000;
		s = (int) (passed % 60);
		passed /= 60;
		m = (int) (passed % 60);
		h = (int) (passed / 60);

		return h + ":" + m + ":" + s;
	}

	/**
	 * 设置window的显示位置为屏幕的中心<br>
	 * 如果window必须已经有size了,那么该窗体将显示在屏幕的中央<br>
	 * 否则它的左上角位于屏幕的中央
	 * 
	 * @param window
	 */
	public static void locOnCenter(Window window) {
		Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wsize = window.getSize();

		int x, y;
		x = (ssize.width - wsize.width) / 2;
		y = (ssize.height - wsize.height) / 2;
		window.setLocation(x, y);
	}
}
