package data.tasks;

import java.io.*;
import java.util.TreeMap;

import data.task.Task;

/**
 * 任务集合的父类
 * 
 * @author lqy
 * 
 * @param <E>
 *            任务的类型
 */
public abstract class TaskMap<E extends Task> {
	/**
	 * 任务集合
	 */
	protected TreeMap<String, E> tasks;
	/**
	 * 任务文件的路径
	 */
	protected String path;

	/**
	 * 读出任务集合
	 * 
	 * @return 文件不存在或有异常则返回false
	 */
	@SuppressWarnings("unchecked")
	public boolean readTasks() {
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
	 * 获取任务
	 * 
	 * @param info
	 *            任务内容
	 * @return 一个任务,一天的任务,一周的任务...
	 */
	public E get(String info) {
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
