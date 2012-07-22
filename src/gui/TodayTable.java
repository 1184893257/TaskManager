package gui;

import java.awt.Color;

import javax.swing.table.TableColumnModel;

import inter.Updater;
import data.TopTaskModel;
import data.task.*;

public class TodayTable extends TopTaskTable<DayTask> {
	private static final long serialVersionUID = 1L;

	public TodayTable(TaskDialog dialog, Updater updater,
			TopTaskTable<WeekTask> father, TopTaskModel<DayTask> model) {
		super(DayTask.class, dialog, 1, true, updater, father, model);

		// 设置表格各列的宽度
		int[] widths = new int[] { 20, 300, 100, 20 };
		TableColumnModel colModel = this.getColumnModel();
		for (int i = 0; i < widths.length; ++i)
			colModel.getColumn(i).setPreferredWidth(widths[i]);

		this.setBackground(Color.LIGHT_GRAY);
		this.setShowVerticalLines(false);
	}

}
