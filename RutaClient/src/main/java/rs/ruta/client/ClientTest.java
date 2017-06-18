package rs.ruta.client;

import java.awt.*;
import java.util.*;

import javax.swing.*;

public class ClientTest
{

	public static void main(String[] args)
	{
		//RutaNode client = new Client();
		Client client = new Client();
		Locale myLocale = Locale.forLanguageTag("sr-RS");
		Locale.setDefault(myLocale);
		client.preInitialize();

		EventQueue.invokeLater(() ->
		{
			JFrame frame = new ClientFrame(client);
			frame.setTitle("Ruta Client");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			client.initialize();
		});
	}
}
