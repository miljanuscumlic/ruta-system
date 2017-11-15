package rs.ruta.client;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.table.*;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.KeywordType;

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
/*		String cellValue = textField.getText();
		List<KeywordType> keywords = null;
		if(cellValue != null)
		{
	//		Arrays.asList(cellValue.split("( )*[,;]+")).stream().map(keyword -> Function<String, KeywordType>);
			keywords = Stream.of(cellValue.split("( )*[,;]+")).map(keyword -> new KeywordType(keyword)).collect(Collectors.toList());
		}
		return keywords;*/

		return textField.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if(column == 7) // keyword list
		{
			String cellValue = "";
			if(value != null)
			{
				cellValue = (String) value;
/*				@SuppressWarnings("unchecked")
				List<KeywordType> keywords = (List<KeywordType>) value;
				cellValue = keywords.stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(" ,"));*/
			}
			textField.setText(cellValue);
		}
		else
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
