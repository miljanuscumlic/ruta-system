package rs.ruta.client;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class PartyTableCellRenderer extends DefaultTableCellRenderer//extends JFormattedTextField implements TableCellRenderer
{
	private JFormattedTextField dateField;

	public PartyTableCellRenderer()
	{
		super();

		//Locale myLocale = Locale.getDefault();
		//DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, myLocale);

		//DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		//DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);


		DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
	    format.setLenient(false);
		dateField = new JFormattedTextField(format);
//		dateField = new JFormattedTextField();
		dateField.setBorder(null);

		//dateField.setText(LocalDate.now().toString());
		//dateField.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(dateFormat)));

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		if(column == 1 && row == 2) //for Date fields = value instanceof some Date class
		{	//if(value instanceof)
			dateField.setInputVerifier(new InputVerifier()
	         {
	            @Override
				public boolean verify(JComponent component)
	            {
	               JFormattedTextField field = (JFormattedTextField) component;
	               try
	               {
	            	   InstanceFactory.getXMLGregorianCalendar(field.getText());
	            	   return true;
	               }
	               catch (Exception e)
	               {
	            	   return false;
	               }
	            }
	         });

			if(value != null)
				dateField.setText(value.toString());
			return dateField;

		}
		else // for all the other fields = value instanceof String
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	}

}
