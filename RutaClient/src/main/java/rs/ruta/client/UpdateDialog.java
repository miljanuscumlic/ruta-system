package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import rs.ruta.common.RutaVersion;
import rs.ruta.services.UpdateRutaClientResponse;

public class UpdateDialog extends JDialog
{
	private static final long serialVersionUID = -1156649727648470904L;
	private Future<?> future = null;
	private JLabel label;
	private URI uri;

	public UpdateDialog(ClientFrame owner, Client client)
	{
		super(owner, "Update check", true);
		setResizable(false);
		setSize(440, 120);
		setLocationRelativeTo(owner);
		JPanel labelPanel = new JPanel();
		label = new JLabel("", SwingConstants.CENTER);
		labelPanel.add(label);
		add(labelPanel, BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel();
		JButton check = new JButton("Check for updates");
		JButton cancel = new JButton("Cancel");
		JButton download = new JButton("Download");
		JButton ok = new JButton("OK");
		getRootPane().setDefaultButton(ok);

		buttonPanel.add(check);
		//		buttonPanel.add(cancel);
		add(buttonPanel, BorderLayout.SOUTH);

		check.addActionListener(event ->
		{
			try
			{
				new Thread( () ->
				{
					EventQueue.invokeLater(() -> label.setText("Please wait. Checking for updates..."));

					future = client.cdrUpdateRutaClient();
					try
					{
						UpdateRutaClientResponse res = (UpdateRutaClientResponse) future.get();
						RutaVersion result = res.getReturn();
						if(result == null)
						{
							EventQueue.invokeLater(() ->
							{
								label.setText("You have the latest version of Ruta Client application.");
								buttonPanel.remove(0); // remove check buuton
								buttonPanel.add(ok);
								repaint();
							});
						}
						else
						{
							EventQueue.invokeLater(() ->
							{
								String link = result.getWeblink();
								try
								{
									uri = new URI(link);

									// create a JEditorPane that renders HTML and defaults to the system font.
									JEditorPane editorPane =  new JEditorPane();//new HTMLEditorKit().getContentType(), "");
									editorPane.setContentType("text/html");
									// set the text of the JEditorPane to the given text.
									editorPane.setText("<html><b>There is a new version " + result.getVersion() +
											" of the Ruta Client application.<br />" + "You can download it at: <a href=\"" +
											link + "\">" + link +"</a><br/>(copy and paste the link in a browser if Download button doesn't work :)</b></html>");

									// add a CSS rule to force body tags to use the default label font
									// instead of the value in javax.swing.text.html.default.csss
									Font font = UIManager.getFont("Label.font");
									String bodyRule = "body { font-family: " + font.getFamily() + "; " +
											"font-size: " + font.getSize() + "pt; }";
									((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(bodyRule);

									editorPane.setOpaque(false);
									editorPane.setBorder(null);
									editorPane.setEditable(false);

									labelPanel.remove(0);
									labelPanel.add(editorPane);

									buttonPanel.remove(0); // remove check buuton
									buttonPanel.add(download);
									buttonPanel.add(cancel);
									revalidate();
									repaint();
								}
								catch (URISyntaxException e)
								{
									EventQueue.invokeLater(() ->
									{
										label.setText("<html><b>There is a new version " + result.getVersion() +
												" of the Ruta Client application.<br />But the download link is malformed." );
										buttonPanel.remove(0); // remove check buuton
										buttonPanel.add(cancel);
										repaint();
									});
								}

							});
						}
					}
					catch (Exception e)
					{
						EventQueue.invokeLater(() ->
						{
							label.setText("<html>There has been an error during update check.<br/>Please try again later.</html>");
							//buttonPanel.remove(0); // remove Check buuton
							buttonPanel.add(cancel);
						});
					}
				}).start();
			}
			catch (Exception e)
			{
				label.setText("<html>There has been an error during update check.<br/>Please try again later.</html>");
				buttonPanel.remove(0); // remove Check buuton
				buttonPanel.add(cancel);
				repaint();
			}
		});

		ok.addActionListener(event ->
		{
			setVisible(false);
		});

		cancel.addActionListener(event ->
		{
			if(future != null)
				future.cancel(true);
			setVisible(false);
		});

		download.addActionListener(event ->
		{
			if (Desktop.isDesktopSupported())
			{
				try
				{
					Desktop.getDesktop().browse(uri);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "Could not open the web browser. Download link might be malformed.", "Error!", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					setVisible(false);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Could not locate the web browser.", "Error!", JOptionPane.ERROR_MESSAGE);
			}
		});

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				if(future != null)
					future.cancel(true);
				setVisible(false);
			}
		});
	}
}
