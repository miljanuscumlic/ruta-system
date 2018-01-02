package rs.ruta.client;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import org.exist.util.MimeTable;
import org.exist.util.MimeType;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.EXistResource;
import javax.swing.*;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XPathQueryService;

public class ClientTest
{

	public static void main(String[] args) throws Exception
	{
		//redirecting error stream to a file
		File file = new File("err.txt");
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
//			System.setErr(ps); //MMM: this sould be uncommented if is wanted to redirect error stream to err.txt
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}

		//setting EXIST_HOME
		final String EXIST_HOME = System.getProperty("user.dir");
		System.setProperty("exist.home", EXIST_HOME);

		//TEST START - write to eXist embedded database
		//initialise the database driver
		@SuppressWarnings("unchecked")
		final Class<Database> dbClass = (Class<Database>) Class.forName("org.exist.xmldb.DatabaseImpl");
		final Database database = dbClass.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);

		Collection coll = null;
		try
		{
			//get the collection
			final String path = "/db/test";
			final String uri = "xmldb:exist://" + path;
			final String username = "admin";
			final String password = null;
			coll = DatabaseManager.getCollection(uri, username, password);

			if(coll == null) {
				//if the collection does not exist, create it!
				//logger.warn("Collection {} does not exist! Creating collection...", path);
				coll = createCollection(uri, username, password);
				//logger.info("Created Collection {}", path);
			}

			final File source = new File("test.jpg");
			//store the document(s) into the collection
			storeDocuments(coll, source);
		}
		finally
		{
			//close the collection
			if(coll != null)
			{
				final DatabaseInstanceManager manager = (DatabaseInstanceManager)coll.getService("DatabaseInstanceManager", "1.0");
				try
				{
					coll.close();
				}
				finally
				{
					//shutdown the database
					manager.shutdown();
				}
			}
		}

		//TEST END

		//RutaNode client = new Client();
		Client client;
		try
		{
			client = new Client();
			Locale myLocale = Locale.forLanguageTag("sr-RS");
			Locale.setDefault(myLocale);

			client.preInitialize();

			EventQueue.invokeLater(() ->
			{
				JFrame frame = new ClientFrame(client);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				client.initialize();
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, /*"Data from the local data store is corrupted!\n" +*/ "Unable to open Ruta Client application.\n" + e.getMessage(), "Critical error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Creates a Collection from an XMLDB URI
	 *
	 * @param uri The XMLDB URI to a Collection to create in the database
	 * @param username
	 * @param password
	 *
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
	 * Creates a collection at a path from the parent collection
	 *
	 * i.e. if the parent collection is "/db" and the pathSegments are ['a', 'b', c']
	 * we will create and return the collection /db/a/b/c
	 *
	 * @param parent the parent collection in which to create the collection path
	 * @param pathSegments Each segment of the path to create, i.e. each segment is a sub-collection
	 *
	 * @return The created collection indicated by the path
	 */
	private static Collection createCollectionPath(final Collection parent, final Queue<String> pathSegments) throws XMLDBException
	{
		if(pathSegments.isEmpty())
			return parent;
		else
		{
			final String subCollectionName = pathSegments.remove();
			final CollectionManagementService mgmtService = (CollectionManagementService)parent.getService("CollectionManagementService", "1.0");
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
			final CollectionManagementService mgmtService = (CollectionManagementService)coll.getService("CollectionManagementService", "1.0");
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
					((EXistResource)res).freeResources();
			}
		}
	}

}
