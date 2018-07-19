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

		//setting EXIST_HOME
		final String EXIST_HOME = System.getProperty("user.dir"); //$NON-NLS-1$
		System.setProperty("exist.home", EXIST_HOME); //$NON-NLS-1$

		final RutaClientFrame frame = new RutaClientFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RutaClient client = null;
		boolean secondTry = false;
		final Semaphore edtSync = new Semaphore(0);
		final JOptionPane awhilePane = new JOptionPane(("Opening Ruta Client application.        \nThis could take a while. Please wait..."), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		final JDialog awhileDialog = awhilePane.createDialog(null, ("Ruta Client")); //$NON-NLS-1$
		awhileDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		final JOptionPane splashPane = new JOptionPane(("Initializing Ruta Client application.        \nPlease wait..."), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		final JDialog splashScreen = splashPane.createDialog(frame, ("Ruta Client")); //$NON-NLS-1$
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
								("Without correct username and password you are not granted access\nto the Ruta Client Application. Application will be closed."), //$NON-NLS-1$
										("Error message"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						System.exit(0);
					});
				}
			}
			catch(Exception e)
			{
				if(e.getMessage() != null && e.getMessage().contains(("Ruta Client application has been already started."))) //$NON-NLS-1$
				{
					secondTry = true;
					EventQueue.invokeLater(() ->
					{
						int option = JOptionPane.showConfirmDialog(null, ("It seems there already has been started one instance of Ruta Client application,\nor the previous instance of the appliation was not closed properly.\nIt might not succeed, but do you still want to try to open a new one?"), //$NON-NLS-1$
								("Ruta Client - Warning"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
						if(option == JOptionPane.YES_OPTION)
							again.set(true);
						else
							again.set(false);
						edtSync.release();
					});
				}
				else
				{
					logger.error(("Fatal error! Data could not be read from the database. Exception is"), e); //$NON-NLS-1$
					EventQueue.invokeLater(() ->
					{
						JOptionPane.showMessageDialog(null, ("Unable to read data from the database.\n") + e.getMessage(), //$NON-NLS-1$
								("Ruta Client - Critical error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
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
									("Without correct username and password you are not granted access\\nto the Ruta Client Application. Application will be closed."), //$NON-NLS-1$
											("Error message"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
							System.exit(0);
						});
					}
				}
				else
				{
					logger.error(("Unable to open Ruta Client application.")); //$NON-NLS-1$
					System.exit(1);
				}
			}

		}
		catch(Exception e)
		{
			awhileDialog.setVisible(false);
			logger.error(("Unable to open Ruta Client application. Exception is "), e); //$NON-NLS-1$
			EventQueue.invokeLater( () ->
			{
				JOptionPane.showMessageDialog(null, ("Unable to open Ruta Client application.\n") + e.getMessage(), //$NON-NLS-1$
						("Ruta Client - Critical error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				System.exit(1);
			});
		}
	}

}