package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import data.TaskModel;
import data.Today;

/**
 * ��ʾ����������
 * 
 * @author lqy
 * 
 */
public class TaskTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * ����ʱ����
	 */
	protected Today today;

	public TaskTable(Today today) {
		super(new TaskModel());
		
		this.today=today;

		//���ü��ѡ��ť
		TableCell cell0 = new TableCell();
		TableColumn column = this.getColumnModel().getColumn(0);
		column.setCellEditor(cell0);
		column.setCellRenderer(cell0);

		//������ɵ�ѡ��ť
		TableCell cell1 = new TableCell();
		column = this.getColumnModel().getColumn(3);
		column.setCellEditor(cell1);
		column.setCellRenderer(cell1);
	}

	protected class TableCell extends AbstractCellEditor implements
			TableCellRenderer, TableCellEditor, ActionListener {
		private static final long serialVersionUID = 1L;

		protected JRadioButton button;

		public TableCell() {
			button = new JRadioButton();
			button.addActionListener(this);
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			return button;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}

	}

}
