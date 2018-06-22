package rs.ruta.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import rs.ruta.common.InstanceFactory;

public class ProductTable extends JTable
{
	private static final long serialVersionUID = 8876600322512003608L;

	public ProductTable(DefaultTableModel tableModel)
	{
		super(tableModel);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableCellEditor getCellEditor(int row, int column)
	{
//		if(row == 6 && column == 1)
//		{
//			final JTextField bigDecimalField = new JTextField();
//			bigDecimalField.setInputVerifier(new BigDecimalVerifier());
//			return new DefaultCellEditor(bigDecimalField);
//		}
//		else
		if(row == 7 && column == 1)
		{
			final JComboBox<String> comboBox = new JComboBox<String>(InstanceFactory.getTaxCategories());
			comboBox.setRenderer(new ComBoxRenderer());
			comboBox.setFont(new JLabel("Test").getFont().deriveFont(Font.PLAIN));
			return new DefaultCellEditor(comboBox);
		}
		else
			return super.getCellEditor(row, column);
	}

	private class ComBoxRenderer extends BasicComboBoxRenderer
	{
		private static final long serialVersionUID = -1039593028193581377L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null)
				setText((String) value);
			setOpaque(true);

			setForeground(Color.BLACK);
			if(isSelected)
				setBackground(Color.LIGHT_GRAY);
			else
				setBackground(Color.WHITE);
			//			component.setFont(component.getFont().deriveFont(Font.PLAIN));
			return this;
		}
	}

	private class BigDecimalVerifier extends InputVerifier
	{
		@Override
		public boolean verify(JComponent input)
		{
			final String text = ((JTextField) input).getText();
			try
			{
				if("".equals(text.trim()))
					return false;
				else
				{
					BigDecimal.valueOf(Double.valueOf(text));
					return true;
				}
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}

		@Override
		public boolean shouldYieldFocus(JComponent input)
		{
			boolean valid = verify(input);
			if (!valid)
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(null, "Invalid decimal number."));
			return valid;
		}
	}

}