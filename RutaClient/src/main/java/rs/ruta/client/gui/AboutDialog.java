package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;

import rs.ruta.client.RutaClient;

public class AboutDialog extends JDialog
{
	private static final long serialVersionUID = 6722375039744654474L;

	//closing dialog by pressing escape key
	private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	public static final String dispatchWindowClosingActionMapKey = "rs.ruta.dialog.about:WINDOW_CLOSING";
	public void installEscapeCloseOperation()
	{
		Action dispatchClosing = new AbstractAction()
		{
			private static final long serialVersionUID = -4733202723801827735L;

			@Override
			public void actionPerformed(ActionEvent event)
			{
				dispatchEvent(new WindowEvent(AboutDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		};
		JRootPane root = getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
		put(escapeStroke, dispatchWindowClosingActionMapKey);
		root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
	}

	public AboutDialog(JFrame owner)
	{
		super(owner, "About Ruta Client", true);
		JLabel label = new JLabel("<html>Ruta Client ver. " + RutaClient.getVersion().getVersion() +
				"<br /> Ruta is an application for global Electronic Data Interchange.</html>",
				SwingConstants.CENTER);
		add(label, BorderLayout.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(event -> setVisible(false));
		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(ok);
		setSize(370, 120);
		setLocationRelativeTo(owner);

		installEscapeCloseOperation();

	}
}
