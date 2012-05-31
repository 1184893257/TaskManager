package gui;

public class FormatTime {
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
}
