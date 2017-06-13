package rs.ruta.client;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;

public class ProductTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
	private static final long serialVersionUID = -2401407150721477314L;
	private JTable table;
	private JTextField textField;

	public ProductTableCellEditor(JTable table)
	{
		this.textField = new JTextField(100);
		this.table = table;
	}

	@Override
	public Object getCellEditorValue()
	{
		return textField.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		textField.setText((String) value);
		return textField;
	}

	@Override
	public boolean stopCellEditing()
	{
		super.stopCellEditing();
		table.repaint();
		return true;
	}

}
