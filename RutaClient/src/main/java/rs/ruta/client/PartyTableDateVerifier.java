package rs.ruta.client;

import java.awt.EventQueue;
import java.text.*;

import javax.swing.*;

public class PartyTableDateVerifier extends InputVerifier
{
	@Override
	public boolean verify(JComponent input)
	{
		JTextField field = (JTextField) input;
		String text = field.getText();

		try //check the validity of the input string of a date
		{
			DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
		    format.setLenient(false);
		    format.parse(text);
		}
		catch (ParseException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean shouldYieldFocus(JComponent input)
	{
		boolean valid = verify(input);
		if (!valid)
			EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(null, "Invalid Date. Date should be in the form: dd.mm.yyyy."));
		return valid;
	}
}