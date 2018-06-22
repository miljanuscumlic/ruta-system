package rs.ruta.client;

import java.awt.EventQueue;
import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.exist.util.MimeTable;
import org.exist.util.MimeType;
import org.exist.xmldb.EXistResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.datamapper.DetailException;

public class RutaClientTest
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");

	public static void main(String[] args) throws Exception
	{
		//setting EXIST_HOME
		final String EXIST_HOME = System.getProperty("user.dir");
		System.setProperty("exist.home", EXIST_HOME);

		final RutaClientFrame frame = new RutaClientFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RutaClient client = null;
		boolean secondTry = false;
		final Semaphore edtSync = new Semaphore(0);
		final JOptionPane awhilePane = new JOptionPane("Opening Ruta Client application.        \n" +
				"This could take a while. Please wait...",
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		final JDialog awhileDialog = awhilePane.createDialog(null, "Ruta Client");
		awhileDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		final JOptionPane splashPane = new JOptionPane("Initializing Ruta Client application.        \nPlease wait...",
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		final JDialog splashScreen = splashPane.createDialog(frame, "Ruta Client");
		splashScreen.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		try
		{
			AtomicBoolean again = new AtomicBoolean();
			try
			{
				client = new RutaClient(frame, false);
				frame.setClient(client);
				client.authorizeUserAccess();
				EventQueue.invokeLater(() -> splashScreen.setVisible(true));
				client.initialize();
				EventQueue.invokeLater(() ->
				{
					frame.initialize();
					splashScreen.setVisible(false);
					frame.setVisible(true);
				});

			}
			catch(Exception e)
			{
				if(e.getMessage() != null && e.getMessage().contains("Ruta Client application has been already started."))
				{
					secondTry = true;
					EventQueue.invokeLater(() ->
					{
						int option = JOptionPane.showConfirmDialog(null, "It seems there already has been started one instance of" +
								" Ruta Client application,\nor the previous instance of the appliation was not closed properly.\n"
								+ "It might not succeed, but do you still want to try to open a new one?",
								"Ruta Client - Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if(option == JOptionPane.YES_OPTION)
							again.set(true);
						else
							again.set(false);
						edtSync.release();
					});
				}
				else
				{
					logger.error("Fatal error! Data could not be read from the database. Exception is ", e);
					EventQueue.invokeLater(() ->
					{
						JOptionPane.showMessageDialog(null, "Unable to read data from the database.\n" + e.getMessage(),
								"Ruta Client - Critical error", JOptionPane.ERROR_MESSAGE);
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
					client.authorizeUserAccess();
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
					logger.error("Unable to open Ruta Client application.");
					System.exit(1);
				}
			}

			final Locale myLocale = Locale.forLanguageTag("sr-RS");
			Locale.setDefault(myLocale);

//			RutaClient aClient = client;

//			EventQueue.invokeLater(() ->
//			{
////				JFrame frame = new RutaClientFrame(aClient);
//				JFrame frame = new RutaClientFrame();
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////				frame.setVisible(true);
//				edtSync.release();
//			});

//			try
//			{
//				edtSync.acquire();
////				aClient.initialize();
//			}
//			catch (DetailException e)
//			{
//				logger.error("Fatal error! Data could not be read from the database. Exception is ", e);
//				EventQueue.invokeLater( () ->
//				{
//					JOptionPane.showMessageDialog(null, "Unable to read data from the database.\n" + e.getMessage(),
//							"Ruta Client - Critical error", JOptionPane.ERROR_MESSAGE);
//					System.exit(1);
//				});
//			}
		}
		catch(Exception e)
		{
			awhileDialog.setVisible(false);
			logger.error("Unable to open Ruta Client application. Exception is ", e);
			EventQueue.invokeLater( () ->
			{
				JOptionPane.showMessageDialog(null, "Unable to open Ruta Client application.\n" + e.getMessage(),
						"Ruta Client - Critical error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			});
		}
	}

	/**
	 * Creates a Collection from an XMLDB URI.
	 * @param uri The XMLDB URI to a Collection to create in the database
	 * @param username
	 * @param password
	 * @return The created Collection
	 */
	private static Collection createCollection(final String uri, final String username, final String password) throws XMLDBException
	{
		if(!uri.startsWith("xmldb:exist:///db"))
			throw new IllegalArgumentException("Invalid Local Database URI: " + uri);

		final Collection dbColl = DatabaseManager.getCollection("xmldb:exist:///db", username, password);
		final Queue<String> subNames = new LinkedList<String>();
		for(String subName : uri.replaceFirst("xmldb:exist:///db", "").split("\\/"))
			subNames.add(subName);

		return createCollectionPath(dbColl, subNames);
	}

	/**
	 * Creates a collection at a path from the clientFrame collection
	 * i.e. if the clientFrame collection is "/db" and the pathSegments are ['a', 'b', c']
	 * we will create and return the collection /db/a/b/c
	 * @param clientFrame the clientFrame collection in which to create the collection path
	 * @param pathSegments Each segment of the path to create, i.e. each segment is a sub-collection
	 * @return The created collection indicated by the path
	 */
	private static Collection createCollectionPath(final Collection parent, final Queue<String> pathSegments) throws XMLDBException
	{
		if(pathSegments.isEmpty())
			return parent;
		else
		{
			final String subCollectionName = pathSegments.remove();
			final CollectionManagementService mgmtService = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
			try
			{
				final Collection subColl = mgmtService.createCollection(subCollectionName);
				return createCollectionPath(subColl, pathSegments);
			}
			finally
			{
				parent.close();
			}
		}
	}

	/**
	 * Stores a File or Directory of Files into a Collection in eXist.
	 * Given a Directory, all files and sub-directories are stored recursively.
	 */
	private static void storeDocuments(final Collection coll, final File source) throws XMLDBException {
		if(source.isDirectory())
		{
			final CollectionManagementService mgmtService = (CollectionManagementService) coll.getService("CollectionManagementService", "1.0");
			final Collection subColl = mgmtService.createCollection(source.getName());
			try
			{
				for(final File f: source.listFiles())
					storeDocuments(subColl, f);
			}
			finally
			{
				//close the collection
				subColl.close();
			}
		}
		else
		{
			//determine the files type
			final MimeTable mimeTable = MimeTable.getInstance();
			final MimeType mimeType = mimeTable.getContentTypeFor(source.getName());
			final String resourceType = mimeType != null && mimeType.isXMLType() ? "XMLResource" : "BinaryResource";

			//store the file
			//logger.info("Starting store of {} to {}/{}...", source.getAbsolutePath(), coll.getName(), source.getName());
			Resource res = null;
			try
			{
				res = coll.createResource(source.getName(), resourceType);
				res.setContent(source);
				coll.storeResource(res);
			}
			finally
			{
				//cleanup resource
				if(res != null)
					((EXistResource) res).freeResources();
			}
		}
	}

}
