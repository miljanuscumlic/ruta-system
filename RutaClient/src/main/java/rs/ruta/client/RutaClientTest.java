package rs.ruta.client;

import java.awt.EventQueue;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ruta.client.gui.RutaClientFrame;

public class RutaClientTest
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client"); //$NON-NLS-1$

	public static void main(String[] args) throws Exception
	{
		final Locale myLocale = Locale.forLanguageTag("sr-RS");
		Locale.setDefault(myLocale);
		Messages.setLocale(myLocale);

		//setting EXIST_HOME
		final String EXIST_HOME = System.getProperty("user.dir"); //$NON-NLS-1$
		System.setProperty("exist.home", EXIST_HOME); //$NON-NLS-1$

		final RutaClientFrame frame = new RutaClientFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RutaClient client = null;
		boolean secondTry = false;
		final Semaphore edtSync = new Semaphore(0);
		final JOptionPane awhilePane = new JOptionPane(Messages.getString("RutaClientTest.3"), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		final JDialog awhileDialog = awhilePane.createDialog(null, Messages.getString("RutaClientTest.4")); //$NON-NLS-1$
		awhileDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		final JOptionPane splashPane = new JOptionPane(Messages.getString("RutaClientTest.5"), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		final JDialog splashScreen = splashPane.createDialog(frame, Messages.getString("RutaClientTest.6")); //$NON-NLS-1$
		splashScreen.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		try
		{
			final AtomicBoolean again = new AtomicBoolean();
			try
			{
				client = new RutaClient(frame, false);
				frame.setClient(client);
				if(client.authorizeUserAccess())
				{
					EventQueue.invokeLater(() -> splashScreen.setVisible(true));
					client.initialize();
					EventQueue.invokeLater(() ->
					{
						frame.initialize();
						splashScreen.setVisible(false);
						frame.setVisible(true);
					});
				}
				else
				{
					client.setEnableStoringProperties(false);
					EventQueue.invokeLater(() ->
					{
						JOptionPane.showMessageDialog(null,
								Messages.getString("RutaClientTest.7"), //$NON-NLS-1$
										Messages.getString("RutaClientTest.8"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						System.exit(0);
					});
				}
			}
			catch(Exception e)
			{
				if(e.getMessage() != null && e.getMessage().contains(Messages.getString("RutaClientTest.9"))) //$NON-NLS-1$
				{
					secondTry = true;
					EventQueue.invokeLater(() ->
					{
						int option = JOptionPane.showConfirmDialog(null, Messages.getString("RutaClientTest.10"), //$NON-NLS-1$
								Messages.getString("RutaClientTest.11"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
						if(option == JOptionPane.YES_OPTION)
							again.set(true);
						else
							again.set(false);
						edtSync.release();
					});
				}
				else
				{
					logger.error(Messages.getString("RutaClientTest.12"), e); //$NON-NLS-1$
					EventQueue.invokeLater(() ->
					{
						JOptionPane.showMessageDialog(null, Messages.getString("RutaClientTest.13") + e.getMessage(), //$NON-NLS-1$
								Messages.getString("RutaClientTest.14"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						System.exit(1);
					});
				}
			}

			if(secondTry)
			{
				edtSync.acquire();
				if(again.get() == true)
				{
					EventQueue.invokeLater(() -> awhileDialog.setVisible(true));
					client = new RutaClient(frame, true);
					frame.setClient(client);
					EventQueue.invokeLater(() -> awhileDialog.setVisible(false));
					if(client.authorizeUserAccess())
					{
						EventQueue.invokeLater(() ->
						{
							awhileDialog.setVisible(false);
							splashScreen.setVisible(true);
						});
						client.initialize();
						EventQueue.invokeLater(() ->
						{
							frame.initialize();
							splashScreen.setVisible(false);
							frame.setVisible(true);
						});
					}
					else
					{
						client.setEnableStoringProperties(false);
						EventQueue.invokeLater(() ->
						{
							JOptionPane.showMessageDialog(null,
									Messages.getString("RutaClientTest.15"), //$NON-NLS-1$
											Messages.getString("RutaClientTest.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
							System.exit(0);
						});
					}
				}
				else
				{
					logger.error(Messages.getString("RutaClientTest.17")); //$NON-NLS-1$
					System.exit(1);
				}
			}

		}
		catch(Exception e)
		{
			awhileDialog.setVisible(false);
			logger.error(Messages.getString("RutaClientTest.19"), e); //$NON-NLS-1$
			EventQueue.invokeLater( () ->
			{
				JOptionPane.showMessageDialog(null, Messages.getString("RutaClientTest.20") + e.getMessage(), //$NON-NLS-1$
						Messages.getString("RutaClientTest.21"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				System.exit(1);
			});
		}
	}

}