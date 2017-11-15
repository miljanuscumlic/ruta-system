package rs.ruta.client;

import java.awt.BorderLayout;

import javax.swing.*;

public class AboutDialog extends JDialog
{
	private static final long serialVersionUID = 6722375039744654474L;

	public AboutDialog(JFrame owner)
	{
		super(owner, "About Ruta Client", true);
		JLabel label = new JLabel("<html>Ruta Client ver. 0.0.1 <br /> Ruta is an application for global Electronic Data Interchange.</html>",
				SwingConstants.CENTER);
		add(label, BorderLayout.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(event -> setVisible(false));
		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, BorderLayout.SOUTH);

		setSize(370, 120);
		setLocationRelativeTo(owner);
	}
}
