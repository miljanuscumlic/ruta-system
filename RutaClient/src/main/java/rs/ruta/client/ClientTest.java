package rs.ruta.client;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.xml.bind.JAXBException;

public class ClientTest
{

	public static void main(String[] args)
	{
		//RutaNode client = new Client();
		Client client = new Client();
		Locale myLocale = Locale.forLanguageTag("sr-RS");
		Locale.setDefault(myLocale);
		try
		{
			client.preInitialize();
		}
		catch(JAXBException e)
		{
			JOptionPane.showMessageDialog(null, "Data from the local data store are corrupted!", "Critical error",
					JOptionPane.ERROR_MESSAGE);
		}

		EventQueue.invokeLater(() ->
		{
			JFrame frame = new ClientFrame(client);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			client.initialize();
		});
	}
}
