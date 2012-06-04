package data.tasks;

import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import data.task.Task;

import static data.StaticData.*;

/**
 * 任务集合的父类
 * 
 * @author lqy
 * 
 * @param <E>
 *            任务的类型
 */
public abstract class TaskMap<E extends Task> {
	public static final String DATA = DATAFOLDER; // 任务集合的存放文件夹名称

	/**
	 * 任务集合
	 */
	protected TreeMap<String, E> tasks;
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
	public abstract void buildDir(String dir);

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
	public void add(E e) {
		tasks.put(e.info, e);
		this.writeTasks(); // 已修改,保存修改
	}

	/**
	 * 删除任务
	 * 
	 * @param info
	 *            要删除的任务的创建时间
	 */
	public void remove(String info) {
		tasks.remove(info);
		this.writeTasks();// 已修改,保存修改
	}
}
