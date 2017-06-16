package rs.ruta.client;

import javax.swing.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class PartyTable extends JTable
{
	private PartyTableCellEditor dateEditor;
	private PartyTableCellEditor stringEditor;

	public PartyTable(AbstractTableModel model)
	{
		super(model);
		dateEditor = new PartyTableDateCellEditor(new PartyTableDateVerifier());
		stringEditor = new PartyTableCellEditor();
	}

	//MMM: NOT USED ANYMORE
	//defining specific cell renderer for the XMLDate.class
	@Override
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		if(row == 3)
			return new PartyTableCellRenderer();
		else
			return super.getCellRenderer(row, column);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column)
	{
		//int modelColumn = convertColumnIndexToModel(column);

		if(row == 3)
			return dateEditor;
		else
			return stringEditor; //super.getCellEditor(row, column);
	}

	public boolean hasChanged()
	{
		return dateEditor.hasChanged() || stringEditor.hasChanged();
	}
}
