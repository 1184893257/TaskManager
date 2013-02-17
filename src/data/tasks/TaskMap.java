package data.tasks;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import data.task.Task;

import static data.StaticData.*;

/**
 * ���񼯺ϵĸ���
 * 
 * @author lqy
 * 
 * @param <E>
 *            �����ϴ洢�����������
 * @param <K>
 *            �����ϵ���һ�������������
 */
public abstract class TaskMap<E extends Task, K extends Task> {
	public static final String DATA = DATAFOLDER; // ���񼯺ϵĴ���ļ�������

	/**
	 * ���񼯺�
	 */
	protected TreeMap<String, E> tasks;
	/**
	 * �����񼯺ϵ���һ�����񼯺�
	 */
	protected TaskMap<K, ? extends Task> father;
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
	protected abstract void buildDir(String dir);

	/**
	 * ��ͬ������һ�����񼯺���ת��δ��ɵ������������
	 * 
	 * @param last
	 *            ͬ������һ�����񼯺�
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
		this.writeTasks();// �ѽ�����޸�д��
		last.writeTasks();// ��������޸�д��
	}

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
	 * ���Ƚ���c��������������
	 * 
	 * @param c
	 *            �Ƚ���,<b>���Ϊnull�򷵻�Ĭ�ϵ�������������ĵ�����</b>
	 * @return ����õ�list�ĵ�����
	 */
	public Iterator<E> iterator(Comparator<Task> c) {
		if (c == null)
			return tasks.values().iterator();

		LinkedList<E> list = new LinkedList<E>(tasks.values());
		Collections.sort(list, c);
		return list.iterator();
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
	public boolean add(E e) {
		if (tasks.get(e.info) == null) {
			tasks.put(e.info, e);
			this.writeTasks(); // ���޸�,�����޸�
			return true;
		}
		JOptionPane.showMessageDialog(null, "����" + e.info + "�Ѵ���", "�������ܾ�",
				JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * ɾ������
	 * 
	 * @param info
	 *            Ҫɾ�������������
	 */
	public void remove(String info) {
		tasks.remove(info);
		this.writeTasks();// ���޸�,�����޸�
	}

	/**
	 * �޸�һ������
	 * 
	 * @param origin
	 *            ԭ���������
	 * @param now
	 *            ���ڵ����
	 */
	public void modify(String origin, E now) {
		remove(origin);
		add(now);
	}

	/**
	 * �����Ƿ�ɱ༭(ɾ��,�޸�)
	 * 
	 * @param info
	 *            ������
	 * @return �ɱ༭����true,���򷵻�false
	 */
	public boolean isTaskEditable(String info) {
		E task = tasks.get(info);
		return task.needTime == 0L;
	}

	/**
	 * ��չinfo����ĵ�����ʱ��
	 * 
	 * @param info
	 *            ����һ��������ַ���
	 * @param time
	 *            Ҫ���ӵ�ʱ��(Ϊ����ʱ�ͱ���˼��ٵ�ʱ����)
	 */
	public void addNeedTime(String info, long time) {
		String up = null; // �����ϼ�������ַ���
		E task = tasks.get(info);
		if (task != null) {
			up = task.father;
			task.needTime += time;
			task.finished = false;
			this.writeTasks(); // �˼����ѱ��޸�
		} else
			up = info;

		if (up != null)
			father.addNeedTime(up, time);
	}

	/**
	 * ��������info�ĳ���ʱ��
	 * 
	 * @param info
	 *            ����һ��������ַ���
	 * @param time
	 *            Ҫ���ӵ�ʱ��(Ϊ����ʱ�ͱ���˼��ٵ�ʱ����)
	 */
	public void addLastTime(String info, long time) {
		String up = null; // �����ϼ�������ַ���
		E task = tasks.get(info);
		if (task != null) {
			up = task.father;
			task.lastTime += time;
			this.writeTasks(); // �˼����ѱ��޸�
		} else
			up = info;

		if (up != null)
			father.addLastTime(up, time);
	}

	/**
	 * ����(��|��|��)�ĵ�һ��
	 */
	public abstract Calendar firstDay(Calendar cal);

	/**
	 * ����(��|��|��)�����һ��
	 */
	public abstract Calendar lastDay(Calendar cal);

	/**
	 * ���ش˼��ϵ����п��ܵ��ϼ��������ļ���
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
	 * ��һ������Ϊ����,�ҵ����������Χ����������׶ε����񼯺�(��ͬһ�������񼯺Ϸ�Χ��)
	 * <p>
	 * ����:calΪ2012/1/1,��ôһ��Month�������getBrothers(cal) �᷵��<br>
	 * "1��"-2012/1/1 "2��"-2012/2/1 ...
	 * 
	 * @param cal
	 *            ���ڶ���
	 * @return
	 */
	public abstract TreeMap<String, Calendar> getBrothers(Calendar cal);

	/**
	 * �����ڶ���ΪԴ����������������������б��е���
	 * <p>
	 * ��EditerDialog�Ĺ��췽�����õ�, ͬʱgetBrothers��Ҳ�����
	 * 
	 * @param cal
	 *            ���ڶ���
	 * @return
	 */
	public abstract String getItemByCal(Calendar cal);

	/**
	 * ��һ������Ϊ����,��õ�ǰ���ĸ�Ҫ(��ʾ��tablePane�ı�����)
	 * 
	 * @param cal
	 * @return
	 */
	public abstract String getPanelBorder(Calendar cal);
}
