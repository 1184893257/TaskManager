package data.tasks;

import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import data.task.Task;

import static data.StaticData.*;

/**
 * ���񼯺ϵĸ���
 * 
 * @author lqy
 * 
 * @param <E>
 *            ���������
 */
public abstract class TaskMap<E extends Task> {
	public static final String DATA = DATAFOLDER; // ���񼯺ϵĴ���ļ�������

	/**
	 * ���񼯺�
	 */
	protected TreeMap<String, E> tasks;
	/**
	 * �����ļ������ļ���<br>
	 * ��{@link #readTasks()}��ʱ���mkdirs����{@link #writeTasks()} д����ȥ<br>
	 * ����������{@link #path}��ʱ��Ҳ����һ��dir
	 */
	protected String dir;
	/**
	 * �����ļ���·��
	 */
	protected String path;

	/**
	 * �������TaskMap��û�е���,���Կ���Ϊ��,ֻ����������Ҫ����{@linkplain #dir}
	 * 
	 * @param dir
	 */
	public abstract void buildDir(String dir);

	/**
	 * �������񼯺�
	 * 
	 * @return �ļ������ڻ����쳣�򷵻�false
	 */
	@SuppressWarnings("unchecked")
	public boolean readTasks() {
		// �ļ��ĸ��ļ��в������򴴽�
		File d = new File(dir);
		if (!d.exists()) {
			d.mkdirs();
			return false;
		}

		// �ļ��������򷵻�
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
	 * �������񼯺�������ĸ���<br>
	 * ���ַ���Ӧ����ÿ�ֽ׶����񼯺϶�Ӧ�߱���
	 * 
	 * @param incFinished
	 *            ��������ɵ�?
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
	 * ������񼯺ϵĵ�����
	 * 
	 * @return
	 */
	public Iterator<Entry<String, E>> iterator() {
		return tasks.entrySet().iterator();
	}

	/**
	 * ��ȡ����
	 * 
	 * @param info
	 *            ��������
	 * @return һ������,һ�������,һ�ܵ�����...
	 */
	public E get(Object info) {
		return tasks.get(info);
	}

	/**
	 * �������
	 * 
	 * @param e
	 *            Ҫ��ӵ�����
	 */
	public void add(E e) {
		tasks.put(e.info, e);
		this.writeTasks(); // ���޸�,�����޸�
	}

	/**
	 * ɾ������
	 * 
	 * @param info
	 *            Ҫɾ��������Ĵ���ʱ��
	 */
	public void remove(String info) {
		tasks.remove(info);
		this.writeTasks();// ���޸�,�����޸�
	}
}
