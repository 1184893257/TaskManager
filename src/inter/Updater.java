package inter;

/**
 * 界面重新布局的接口
 * 
 * @author lqy
 * 
 */
public interface Updater {
	/**
	 * 界面内的某个表格有数据变化,要更新表格的高以完整显示表格
	 */
	public void update();
}
