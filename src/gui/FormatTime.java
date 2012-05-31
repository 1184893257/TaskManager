package gui;

public class FormatTime {
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
}
