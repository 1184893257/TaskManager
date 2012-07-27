package gui;

import java.awt.*;

public class StaticMethod {
	/**
	 * ��ʱ����passed(��λ:����)ת��ΪHH:MM:SS�ĸ�ʽ
	 * 
	 * @param passed
	 *            ʱ����(��λ:����)
	 * @return HH:MM:SS��ʽ��ʱ��
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
	 * ����window����ʾλ��Ϊ��Ļ������<br>
	 * ���window�����Ѿ���size��,��ô�ô��彫��ʾ����Ļ������<br>
	 * �����������Ͻ�λ����Ļ������
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
