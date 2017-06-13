package rs.ruta.client;

import java.text.*;

import javax.swing.*;

public class PartyTableDateVerifier extends InputVerifier
{
	@Override
	public boolean verify(JComponent input)
	{
		JTextField field = (JTextField) input;
		String text = field.getText();

		try //check the validity of the input string for the date
		{
			DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
		    format.setLenient(false);
		    format.parse(text);
		} catch (ParseException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean shouldYieldFocus(JComponent input)
	{
		boolean valid = verify(input);
		if (!valid) {
			JOptionPane.showMessageDialog(null, "Invalid Date");
		}
		return valid;
	}
}
