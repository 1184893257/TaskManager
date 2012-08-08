package gui;

import static gui.StaticMethod.locOnCenter;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import data.TopTaskModel;
import data.task.*;

public class TaskDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * ��ζԻ���ȡ����,���ݲ�����
	 */
	private boolean canceled;
	/**
	 * �Ի������ı༭���(һ������)
	 */
	public Task task;
	/**
	 * ������޸�����,�������������޸ĵ����������
	 */
	public String modifyInfo;

	// ��ȡ��Ϣ�����
	/**
	 * ��д�������ݵ�text
	 */
	protected JTextField infoText;
	/**
	 * ��дԤ����Ҫʱ���Сʱ����
	 */
	protected JTextField hourText;
	/**
	 * ��дԤ����Ҫʱ��ķ��Ӳ���
	 */
	protected JTextField minuteText;
	/**
	 * �ɹ�ѡ������и�����,��һ������(��ʾû�и�����)
	 */
	protected JComboBox<String> fathers;

	// �����������װ��JPanel��,֧���Ȳ岥O(��_��)O~
	protected JPanel infoPanel;
	protected JPanel timePanel;
	protected JPanel fatherPanel;
	protected JButton ok;

	public TaskDialog(Window owner) {
		super(owner, ModalityType.DOCUMENT_MODAL);

		this.setLayout(new GridBagLayout());// ���ò��ֹ�����

		this.buildInfoPanel();
		this.buildTimePanel();
		this.buildFatherPanel();

		// ���ȷ����ť
		ok = new JButton("ȷ��");
		ok.addActionListener(this);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
				TaskDialog.this.setVisible(false);
			}
		});

		locOnCenter(this);// TaskDialog���Ͻǵĳ�ʼ��ʾλ��Ϊ��Ļ������
	}

	/**
	 * �������ݲ��
	 */
	protected void buildInfoPanel() {
		this.infoPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.infoPanel.setLayout(layout);

		// ������������ı���
		JLabel label = new JLabel("��������:");
		layout.setConstraints(label, c);
		this.infoPanel.add(label);
		infoText = new JTextField("��������������", 20);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		layout.setConstraints(infoText, c);
		this.infoPanel.add(infoText);
	}

	/**
	 * ʱ����
	 */
	protected void buildTimePanel() {
		this.timePanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.timePanel.setLayout(layout);

		// �������ʱ���ǩ
		JLabel label = new JLabel("����ʱ��:");
		layout.setConstraints(label, c);
		this.timePanel.add(label);

		// ���Сʱ�����
		hourText = new JTextField("0", 2);
		c.gridx = GridBagConstraints.RELATIVE;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(hourText, c);
		this.timePanel.add(hourText);

		label = new JLabel("Сʱ");
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		this.timePanel.add(label);

		// ��ӷ��������
		minuteText = new JTextField("3", 2);
		c.weightx = 0.5;
		layout.setConstraints(minuteText, c);
		this.timePanel.add(minuteText);

		label = new JLabel("����");
		c.weightx = 0.0;
		layout.setConstraints(label, c);
		this.timePanel.add(label);
	}

	/**
	 * ��������
	 */
	protected void buildFatherPanel() {
		this.fatherPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.fatherPanel.setLayout(layout);

		JLabel label = new JLabel("������:");
		layout.setConstraints(label, c);
		this.fatherPanel.add(label);

		// ��������б��
		fathers = new JComboBox<String>();
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		layout.setConstraints(fathers, c);
		this.fatherPanel.add(fathers);
	}

	/**
	 * �����ֲ����װ����,����ʾ
	 * 
	 * @param fathers
	 *            ����������
	 * @param index
	 *            �������λ��
	 */
	protected void packPanels(LinkedList<String> fathers, int index) {
		// �������֮ǰ�ķ��õĲ��
		Container con = this.getContentPane();
		con.removeAll();

		// ���GridBagLayout���ֹ�����(�ڹ����ʱ�����õ�)
		GridBagLayout layout = (GridBagLayout) con.getLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		// ����Ҫ��ӵ��������ݲ��
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 0.5;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(infoPanel, c);
		add(infoPanel);

		// ��������е�ʱ����
		c.gridy = GridBagConstraints.RELATIVE;
		if (task.getClass() == DayTask.class) {
			layout.setConstraints(timePanel, c);
			add(timePanel);
		}

		// �������û�еĸ�������
		if (task.getClass() != YearTask.class) {
			this.fathers.removeAllItems();
			this.fathers.addItem("<��>");
			for (String item : fathers)
				this.fathers.addItem(item);
			this.fathers.setSelectedIndex(index);// ����ѡ�еĸ�����
			layout.setConstraints(fatherPanel, c);
			add(fatherPanel);
		}

		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(ok, c);
		add(ok);
		this.getRootPane().setDefaultButton(ok);
		pack();
		this.setVisible(true);
	}

	/**
	 * ��ʾ�������Ի���
	 * 
	 * @param cla
	 *            Ҫ��ӵ���������͵�Class����
	 * @param fathers
	 *            ������ɼ̳еĸ�����
	 * @return �������false��ʾ��ζԻ���ȡ����,���ݲ�����
	 */
	public boolean showAddDialog(Class<? extends Task> cla,
			LinkedList<String> fathers) {
		this.setTitle("���������������Ϣ");
		canceled = false;

		if (cla == DayTask.class)
			task = new DayTask("NULL", 0L, null);
		else if (cla == WeekTask.class)
			task = new WeekTask("NULL", null);
		else if (cla == MonthTask.class)
			task = new MonthTask("NULL", null);
		else if (cla == YearTask.class)
			task = new YearTask("NULL");
		else {
			JOptionPane.showMessageDialog(this, "δ֪����������:" + cla, "���������",
					JOptionPane.ERROR_MESSAGE);
			canceled = true;
			return false;
		}

		packPanels(fathers, 0);// 0��"<��>"
		return !canceled;
	}

	/**
	 * ��ʾ�޸�����Ի���
	 * 
	 * @param task
	 *            Ҫ�޸ĵ�����
	 * @param fathers
	 *            ������ɼ̳еĸ�����
	 * @return �������false��ʾ��ζԻ���ȡ����,���ݲ�����
	 */
	public boolean showEditDialog(Task task, LinkedList<String> fathers) {
		this.setTitle("���޸Ĵ����������");
		canceled = false;
		this.modifyInfo = task.info;
		try {
			this.task = (Task) task.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// ��֮ǰ����Ϣ��ʾ����,�û�ֻ����Ҫ�޸ĵĲ���
		infoText.setText(task.info);// ԭ������������
		long need = task.needTime / 1000L / 60L;
		int minute = (int) (need % 60);
		int hour = (int) (need / 60);
		hourText.setText(Integer.toString(hour));// ԭ����Сʱ
		minuteText.setText(Integer.toString(minute));// ԭ���ķ�

		packPanels(fathers, fathers.indexOf(task.father) + 1);// +1����Ϊ����һ��"<��>"
		return !canceled;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.canceled = false;

		task.info = infoText.getText();
		task.needTime = Integer.parseInt(hourText.getText()) * 3600 * 1000
				+ Integer.parseInt(minuteText.getText()) * 60 * 1000;
		task.father = this.fathers.getSelectedIndex() == 0 ? null
				: (String) this.fathers.getSelectedItem();
		if (task.getClass() != DayTask.class || task.needTime > 0L) // ����ʱ�䲻Ϊ0
			;
		else {
			this.canceled = true;
			JOptionPane.showMessageDialog(this, "����ʱ��������0", "�������",
					JOptionPane.WARNING_MESSAGE);
		}

		// �������������,��������ʱ��
		task.needTime = task.getClass() == DayTask.class ? task.needTime : 0L;
		if (task.info.equals(TopTaskModel.TOTAL)) {
			canceled = true;
			JOptionPane.showMessageDialog(this, "\"" + TopTaskModel.TOTAL
					+ "\"" + "Ϊ�����ֲ�����Ϊ��������", "�������",
					JOptionPane.WARNING_MESSAGE);
		}

		this.setVisible(canceled);
	}
}
