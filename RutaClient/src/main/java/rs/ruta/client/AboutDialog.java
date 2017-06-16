package rs.ruta.client;

import java.awt.BorderLayout;

import javax.swing.*;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog
{
	public AboutDialog(JFrame owner)
	{
		super(owner, "About Ruta", true);
		add(new JLabel("Ruta ver. 0.1"), BorderLayout.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(event -> setVisible(false));
		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, BorderLayout.SOUTH);


		setSize(150, 100);
		setLocationRelativeTo(owner);
	}
}
