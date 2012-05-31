package data.tasks;

import java.io.*;
import java.util.TreeMap;

import data.task.Task;

/**
 * ���񼯺ϵĸ���
 * 
 * @author lqy
 * 
 * @param <E>
 *            ���������
 */
public abstract class TaskMap<E extends Task> {
	/**
	 * ���񼯺�
	 */
	protected TreeMap<String, E> tasks;
	/**
	 * �����ļ���·��
	 */
	protected String path;

	/**
	 * �������񼯺�
	 * 
	 * @return �ļ������ڻ����쳣�򷵻�false
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
	 * д�����񼯺�
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
	 * �������
	 * 
	 * @param e
	 *            Ҫ��ӵ�����
	 */
	public void add(E e) {
		tasks.put(e.info, e);
	}

	/**
	 * ɾ������
	 * 
	 * @param info
	 *            Ҫɾ��������Ĵ���ʱ��
	 */
	public void remove(String info) {
		tasks.remove(info);
	}
}
