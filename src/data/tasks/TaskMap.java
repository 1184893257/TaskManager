package data.tasks;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import data.task.Task;

import static data.StaticData.*;

/**
 * 任务集合的父类
 * 
 * @author lqy
 * 
 * @param <E>
 *            本集合存储的任务的类型
 * @param <K>
 *            本集合的上一级的任务的类型
 */
public abstract class TaskMap<E extends Task, K extends Task> {
	public static final String DATA = DATAFOLDER; // 任务集合的存放文件夹名称

	/**
	 * 任务集合
	 */
	protected TreeMap<String, E> tasks;
	/**
	 * 此任务集合的上一级任务集合
	 */
	protected TaskMap<K, ? extends Task> father;
	/**
	 * 任务文件所在文件夹<br>
	 * 在{@link #readTasks()}的时候会mkdirs以免{@link #writeTasks()} 写不进去<br>
	 * 所以在设置{@link #path}的时候也设置一下dir
	 */
	protected String dir;
	/**
	 * 任务文件的路径
	 */
	protected String path;

	/**
	 * 这个方法TaskMap中没有调用,所以可以为空,只是提醒子类要设置{@linkplain #dir}
	 * 
	 * @param dir
	 */
	protected abstract void buildDir(String dir);

	/**
	 * 从同级的上一个任务集合中转移未完成的任务到这个集合
	 * 
	 * @param last
	 *            同级的上一个任务集合
	 */
	protected void bringLast(TaskMap<E, K> last) {
		Iterator<Entry<String, E>> it = last.iterator();
		E d;
		while (it.hasNext()) {
			d = it.next().getValue();
			if (!d.finished)
				if (d.lastTime == d.needTime)
					d.finished = true;
				else {
					tasks.put(d.info, d);
					it.remove();
				}
		}
		this.writeTasks();// 把今天的修改写入
		last.writeTasks();// 把昨天的修改写入
	}

	/**
	 * 读出任务集合
	 * 
	 * @return 文件不存在或有异常则返回false
	 */
	@SuppressWarnings("unchecked")
	public boolean readTasks() {
		// 文件的父文件夹不存在则创建
		File d = new File(dir);
		if (!d.exists()) {
			d.mkdirs();
			return false;
		}

		// 文件不存在则返回
		File f = new File(path);
		if (!f.exists())
			return false;

		try {
			ObjectInputStream in = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(f)));
			tasks = (TreeMap<String, E>) in.readObject();
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 写入任务集合
	 */
	public void writeTasks() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(path)));
			out.writeObject(tasks);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回任务集合中任务的个数<br>
	 * 这种方法应该是每种阶段任务集合都应具备的
	 * 
	 * @param incFinished
	 *            包括已完成的?
	 * @return
	 */
	public int getSize(boolean incFinished) {
		if (incFinished)
			return tasks.size();

		int size = 0;
		Iterator<Entry<String, E>> it = tasks.entrySet().iterator();
		while (it.hasNext())
			if (!it.next().getValue().finished)
				size++;
		return size;
	}

	/**
	 * 获得任务集合的迭代器
	 * 
	 * @return
	 */
	public Iterator<Entry<String, E>> iterator() {
		return tasks.entrySet().iterator();
	}

	/**
	 * 按比较器c的升序排列任务
	 * 
	 * @param c
	 *            比较器,<b>如果为null则返回默认的以任务名排序的迭代器</b>
	 * @return 排序好的list的迭代器
	 */
	public Iterator<E> iterator(Comparator<Task> c) {
		if (c == null)
			return tasks.values().iterator();

		LinkedList<E> list = new LinkedList<E>(tasks.values());
		Collections.sort(list, c);
		return list.iterator();
	}

	/**
	 * 获取任务
	 * 
	 * @param info
	 *            任务内容
	 * @return 一个任务,一天的任务,一周的任务...
	 */
	public E get(Object info) {
		return tasks.get(info);
	}

	/**
	 * 添加任务
	 * 
	 * @param e
	 *            要添加的任务
	 */
	public boolean add(E e) {
		if (tasks.get(e.info) == null) {
			tasks.put(e.info, e);
			this.writeTasks(); // 已修改,保存修改
			return true;
		}
		JOptionPane.showMessageDialog(null, "任务" + e.info + "已存在", "操作被拒绝",
				JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * 删除任务
	 * 
	 * @param info
	 *            要删除的任务的名称
	 */
	public void remove(String info) {
		tasks.remove(info);
		this.writeTasks();// 已修改,保存修改
	}

	/**
	 * 修改一个任务
	 * 
	 * @param origin
	 *            原任务的名称
	 * @param now
	 *            现在的情况
	 */
	public void modify(String origin, E now) {
		remove(origin);
		add(now);
	}

	/**
	 * 任务是否可编辑(删除,修改)
	 * 
	 * @param info
	 *            任务名
	 * @return 可编辑返回true,否则返回false
	 */
	public boolean isTaskEditable(String info) {
		E task = tasks.get(info);
		return task.needTime == 0L;
	}

	/**
	 * 扩展info任务的的所需时间
	 * 
	 * @param info
	 *            代表一个任务的字符串
	 * @param time
	 *            要增加的时间(为负数时就变成了减少的时间了)
	 */
	public void addNeedTime(String info, long time) {
		String up = null; // 代表上级任务的字符串
		E task = tasks.get(info);
		if (task != null) {
			up = task.father;
			task.needTime += time;
			task.finished = false;
			this.writeTasks(); // 此集合已被修改
		} else
			up = info;

		if (up != null)
			father.addNeedTime(up, time);
	}

	/**
	 * 增加任务info的持续时间
	 * 
	 * @param info
	 *            代表一个任务的字符串
	 * @param time
	 *            要增加的时间(为负数时就变成了减少的时间了)
	 */
	public void addLastTime(String info, long time) {
		String up = null; // 代表上级任务的字符串
		E task = tasks.get(info);
		if (task != null) {
			up = task.father;
			task.lastTime += time;
			this.writeTasks(); // 此集合已被修改
		} else
			up = info;

		if (up != null)
			father.addLastTime(up, time);
	}

	/**
	 * 本年(月|周|日)的第一天
	 */
	public abstract Calendar firstDay(Calendar cal);

	/**
	 * 本年(月|周|日)的最后一天
	 */
	public abstract Calendar lastDay(Calendar cal);

	/**
	 * 返回此集合的所有可能的上级任务名的集合
	 * 
	 * @return
	 */
	public LinkedList<String> getFathers() {
		LinkedList<String> ans = new LinkedList<String>();
		if (father != null) {
			for (K e : father.tasks.values())
				ans.add(e.info);
			ans.addAll(father.getFathers());
		}
		return ans;
	}

	/**
	 * 以一个日期为线索,找到这个日期周围的所有这个阶段的任务集合(在同一个父任务集合范围内)
	 * <p>
	 * 例如:cal为2012/1/1,那么一个Month对象调用getBrothers(cal) 会返回<br>
	 * "1月"-2012/1/1 "2月"-2012/2/1 ...
	 * 
	 * @param cal
	 *            日期对象
	 * @return
	 */
	public abstract TreeMap<String, Calendar> getBrothers(Calendar cal);

	/**
	 * 以日期对象为源获得其代表的任务表格在下拉列表中的项
	 * <p>
	 * 在EditerDialog的构造方法中用到, 同时getBrothers中也会调用
	 * 
	 * @param cal
	 *            日期对象
	 * @return
	 */
	public abstract String getItemByCal(Calendar cal);

	/**
	 * 以一个日期为线索,求得当前表格的概要(显示在tablePane的边线上)
	 * 
	 * @param cal
	 * @return
	 */
	public abstract String getPanelBorder(Calendar cal);
}
