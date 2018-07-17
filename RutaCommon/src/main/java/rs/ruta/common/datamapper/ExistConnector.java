package rs.ruta.common.datamapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.exist.security.Account;
import org.exist.util.MimeTable;
import org.exist.util.MimeType;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**
 * ExistConnector encapsulates common data and methods related to the connection to eXist database.
 */
public class ExistConnector implements DatastoreConnector
{
	private static String databaseId = "exist";
	private static String uriPrefix = "xmldb:" + databaseId + "://";
	private static String existRootCollectionPath = "/db";
	private static String rutaDevelopCollectionPath = existRootCollectionPath + "/apps/ruta-develop"; // path of the application's base collection in snapshot version
	private static String rutaReleaseCollectionPath = existRootCollectionPath + "/apps/ruta"; // path of the application's base collection in release version
	private static String rutaCollectionPath = rutaReleaseCollectionPath; //rutaDevelopCollectionPath // path of the application's base collection
	private static String uriSufix = "/exist/xmlrpc";
	private static String server = "localhost";
	private static String port = "8888";
	private static String baseUri = uriPrefix + server + ":" + port + uriSufix;
	private static String dbJarPath = "C:\\Programs\\exist-db\\start.jar"; // path to the database jar archive
	private static String docSufix = ".xml";
	private static String deletedPath = "/deleted";
	private static String queryPath = "/system/xquery";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.common");
	private static boolean connected = false;

	public ExistConnector() { }

	/**
	 * Constructs eXist database instance and registers it at the {@link DatabaseManager}, enabling
	 * the application to communicate with it.
	 * @throws DatabaseException if fails to connect to the database
	 */
	@Override
	public void connectToDatabase() throws DatabaseException
	{
		if(!connected)
		{
			try
			{
				@SuppressWarnings("unchecked")
				final Class<Database> dbClass = (Class<Database>) Class.forName("org.exist.xmldb.DatabaseImpl");
				final Database database = dbClass.newInstance();
				database.setProperty("create-database", "true");
				DatabaseManager.registerDatabase(database);
			}
			catch(Exception e)
			{
				logger.error("Exception is ", e);
				throw new DatabaseException("Database could not be registered with the database manager.");
			}
		}
		//checks the connection to be sure it's open
		if(isDatabaseAccessible())
			connected = true;
		else
		{
			logger.error("Database connectivity issue. Database is not accessible. username: " + DatabaseAdmin.getInstance().getUsername() +
					" password: " + DatabaseAdmin.getInstance().getPassword());
			throw new DatabaseException("Database connectivity issue. Database is not accessible.");
		}
	}

	/**Checks whether the database is accessible. If it is not shuts down the database.
	 * @return true or false
	 */
	private boolean isDatabaseAccessible()
	{
		boolean access = true;
		try
		{
//			getExistRootCollection();
			checkCollection(""/*rutaCollectionPath*/);
		}
		catch (/*XMLDBException |*/ DatabaseException e)
		{
			access = false;
			logger.error("Exception is ", e);
			try
			{
				shutdownDatabase();
			}
			catch (Exception e1)
			{
				logger.error("Exception is ", e1);
			}
		}
		return access;
	}

	/**
	 * Starts the eXist database application in its own jetty server on this machine.
	 * @throws Exception if eXist could not be started
	 */
	public static void startDatabase() throws Exception
	{
		new Thread( () ->
		{
			Process process;
			try
			{
				logger.info("RutaUser directory: " + System.getProperty("user.dir"));
				logger.info("RutaUser home: " + System.getProperty("user.home"));

				String[] commands = {"cmd", "/c", "start", "cmd", "/k", "java", "-Xmx1024M", "-Dexist.home=C:\\Programs\\exist-db",
						"-Djava.endorsed.dirs=lib/endorsed", "-Djetty.port=8888", "-jar", "C:\\Programs\\exist-db\\start.jar", "jetty"};
				ProcessBuilder probuilder = new ProcessBuilder(commands);
				process = probuilder.start();

/*			String command = "cmd /c start cmd /k java -Xmx1024M -Dexist.home=C:\\Programs\\exist-db -Djava.endorsed.dirs=lib/endorsed -Djetty.port=8888 -jar C:\\Programs\\exist-db\\start.jar jetty";
			System.out.println("Exist home: " + System.getProperty("exist.home"));
			//logger.info(command);
			System.out.println(command);
			process = Runtime.getRuntime().exec(command);*/

				printLines("info", process.getInputStream());
				printLines("error", process.getErrorStream());
				//process.waitFor(2000, TimeUnit.SECONDS);
				//process.destroyForcibly();

				//process.waitFor();
				//logger.info("exitValue() " + process.exitValue());
				//System.out.println("exitValue() " + process.exitValue());
			}
			catch (Exception e)
			{
				logger.error("Could not start the database. Exception is " , e);
			}
		}).start();
	}

	/**
	 * Sends <code>Inpustream</code> text to the console window.
	 * @param type type of the message
	 * @param ins <code>Inpustream</code> that would be sent to the console window
	 * @throws Exception if there is the error in starting the database or reading the input stream
	 */
	private static void printLines(String type, InputStream ins) throws Exception
	{
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		if("info".equals(type))
			while ((line = in.readLine()) != null)
				System.out.println(line);
		else if("error".equals(type))
		{
			while((line = in.readLine()) != null)
				System.err.println(line);
			throw new Exception("Error in starting the database.");
		}
		else
			throw new Exception("Invalid type of the input stream.");
	}

	/**
	 * Shuts down eXist database, its application program and jetty server as its container.
	 * @throws Exception if database could not be stopped
	 */
	@Override
	public void shutdownDatabase() throws Exception
	{
		Collection root = null;
		try
		{
			root = getRootCollection();
			final DatabaseInstanceManager dbm = (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
			dbm.shutdown();
			connected = false;
		}
		catch (XMLDBException e)
		{
			logger.error("Could not shutdown the database. Exception is ", e);
			throw e;
		}
	}

	/**
	 * Restarts the eXist database and its jetty container.
	 * @throws Exception if eXist cannot be restarted
	 */
	public void restartDatabase() throws Exception
	{
		shutdownDatabase();
		startDatabase();
	}

	/**
	 * Retrieves the URI prefix of the eXist database server
	 * @return the uriPrefix
	 */
	public static String getUriPrefix()
	{
		return uriPrefix;
	}
	/**
	 * Sets the URI prefix of the eXist database server
	 * @param uriPrefix the uriPrefix to set
	 */
	public static void setUriPrefix(String uriPrefix)
	{
		ExistConnector.uriPrefix = uriPrefix;
	}

	/**Retrieves the URI sufix of the eXist database server
	 * @return the uriSufix
	 */
	public static String getUriSufix()
	{
		return uriSufix;
	}

	/**Sets the URI sufix of the eXist database server
	 * @param uriSufix the uriSufix to set
	 */
	public static void setUriSufix(String uriSufix)
	{
		ExistConnector.uriSufix = uriSufix;
	}

	/**Retrieves the IP of the eXist database server
	 * @return the server
	 */
	public static String getServer()
	{
		return server;
	}

	/**
	 * Sets the IP of the eXist database server
	 * @param server the server to set
	 */
	public static void setServer(String server)
	{
		ExistConnector.server = server;
	}

	/**
	 * Retrieves the port of the eXist database server
	 * @return the port
	 */
	public static String getPort()
	{
		return port;
	}

	/**
	 * Sets the port of the eXist database server
	 * @param port the port to set
	 */
	public static void setPort(String port)
	{
		ExistConnector.port = port;
	}

	/**
	 * Returns base part of the URI of the eXist database.
	 * @return the uri of the eXist database
	 */
	public static String getBaseUri()
	{
		return baseUri;
	}

	/**
	 * Sets static fields so that the local XMLDB API is called.
	 */
	public void setLocalAPI()
	{
		baseUri = uriPrefix;
	}

	/**
	 * Gets the extension of the document name i.e. it can be .xml.
	 */
	public static String getDocumentSufix() { return docSufix; }

	/**Sets the extension of the document name i.e. it can be ".xml"
	 * @param docSufix sufix to be set
	 */
	public static void setDocumentSufix(String docSufix) { ExistConnector.docSufix = docSufix; }

	/**
	 * Gets the collection from the database as a database admin. Relative path to retrieved collection
	 * is passed as a argument.
	 * @param collectionPath collection path relative to application's root collection
	 * @return a {@code Collection} instance for the requested collection or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getCollection(String collectionPath) throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + collectionPath,
				DatabaseAdmin.getInstance().getUsername(), DatabaseAdmin.getInstance().getPassword());
	}

	/**
	 * Gets the collection from the database as a specified user. Collection that is retrieved
	 * has its relative path passed as a parameter.
	 * @param collectionPath collection path relative to application's root collection
	 * @param username
	 * @param password
	 * @return a {@code Collection} instance for the requested collection or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getCollection(String collectionPath, String username, String password) throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + collectionPath, username, password);
	}

	/**
	 * Gets root collection of the eXist database as database admin.
	 * @return root Collection instance or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getExistRootCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(baseUri + existRootCollectionPath, DatabaseAdmin.getInstance().getUsername(),
				DatabaseAdmin.getInstance().getPassword());
	}

	/**
	 * Gets main collection of Ruta application in the database as database admin.
	 * @return root Collection instance or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getRootCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(baseUri + rutaCollectionPath, DatabaseAdmin.getInstance().getUsername(),
				DatabaseAdmin.getInstance().getPassword());
	}

	/**
	 * Gets root collection from the database as a specified user. Collection that is retrieved
	 * is defined in the subclasses of the XmlMapper.
	 * @return root Collection instance or {@code null} if the collection could not be found.
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getRootCollection(String username, String password) throws XMLDBException
	{
		return DatabaseManager.getCollection(baseUri + rutaCollectionPath, username, password);
	}

	@Override
	public boolean checkUser(String username, String password) throws DatabaseException
	{
		try
		{
			getRootCollection(username, password);
			return true;
		}
		catch(XMLDBException e)
		{
			if(e.errorCode == ErrorCodes.PERMISSION_DENIED)
				return false;
			else
				throw new DatabaseException("Could not connect to the database", e);
		}
	}

	/**
	 * Gets the collection from the database as a database admin. If collection does not exist, method creates it.
	 * @param collectionPath collection path relative to application's root collection
	 * @return a {@code Collection} instance for the requested collection path
	 * @throws XMLDBException if the collection could not be retrieved or created
	 */
	public static Collection getOrCreateCollection(String collectionPath) throws XMLDBException
	{
		return getOrCreateCollection(getBaseUri(), getRelativeRutaCollectionPath() + collectionPath,
				DatabaseAdmin.getInstance().getUsername(), DatabaseAdmin.getInstance().getPassword());
	}

	/**
	 * Gets the collection from the database. If collection does not exist, method creates it. The user
	 * credentials on whose behalf collection is retrieved are passed as parameters.
	 * @param uri absolute URI defining the base collection on which the subcollection relative path is appended
	 * @param collectionUri relative path of the subcollection that is actually retrived
	 * @param username user's username of whose behalf collection is retrieved
	 * @param password user's password
	 * @r{@code Collection}n</code> instance for the requested collection
	 * @throws XMLDBException if collection could not be retrieved or created
	 */
	protected static Collection getOrCreateCollection(final String uri, final String collectionUri,
			final String username, final String password) throws XMLDBException
	{
		//forming the queue of path segments that represent each collection on the path
		final Queue<String> segments = new ArrayDeque<String>();
		for(final String pathSegment : collectionUri.split("/"))
			if(!pathSegment.isEmpty())
				segments.add(pathSegment);
		//if missing creates all collections on the path
		Collection current = DatabaseManager.getCollection(uri + "/" + segments.poll(), username, password);
		if(current == null) // root collection (/db) does not exist and can not be created
			throw new XMLDBException();
		return getOrCreateCollection(current, segments);
	}

	/**
	 * Creates all subcollections of the passed {@code Collection} argument.
	 * @param current {@code Collection} which subcollections are created
	 * @param descendants all subcollections that should be created or traversed on the path
	 * @return a {@code Collection} instance for the passed collection
	 * @throws XMLDBException if collection could not be retrieved or created
	 */
	private static Collection getOrCreateCollection(final Collection current, final Queue<String> descendants) throws XMLDBException
	{
		if(descendants.isEmpty())
			return current;
		else
		{
			final String childName = descendants.poll();
			Collection child = current.getChildCollection(childName);
			if(child == null) // collection does not exist -> create it
			{
				final CollectionManagementService mgmt =
						(CollectionManagementService) current.getService("CollectionManagementService", "1.0");
				child = mgmt.createCollection(childName);
				current.close(); //close the current collection, child will remain open
			}
			//recursively creates all other subcollection on the path i.e. in the queue
			return getOrCreateCollection(child, descendants);
		}
	}

	/**
	 * Deletes {@link Collection} and all content in it.
	 * @param parent parent collection of the collection to be deleted
	 * @param collectionName collection's name
	 * @throws XMLDBException if collection could not be deleted or parent collection could not be closed
	 */
	protected static void deleteCollection(final Collection parent, String collectionName) throws XMLDBException
	{
		final CollectionManagementService mgmt =
				(CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
		mgmt.removeCollection(collectionName);
	}

	/**
	 * Deletes {@link Collection} and all content in it.
	 * @param collection collection to be deleted
	 * @throws XMLDBException if collection could not be deleted or parent collection could not be closed
	 */
	protected static void deleteCollection(final Collection collection) throws XMLDBException
	{
		final Collection parent = collection.getParentCollection();
		deleteCollection(parent, collection.getName());
		parent.close();
	}

	/**
	 * Checks if the collection exist. If not, method creates the collection with passed user credentials as an owner of the collection.
	 * @param collectionPath relative collection path
	 * @param username user's username
	 * @param password user's password
	 * @throws XMLDBException if collection could not be created or retrieved
	 */
	protected void checkCollection(String collectionPath, String username, String password) throws XMLDBException
	{
		getOrCreateCollection(getBaseUri(), getRelativeRutaCollectionPath() + collectionPath, username, password);
	}

	/**
	 * Checks whether the collection exists. If not, method creates the collection and asigns database admin
	 * as an owner of the collection.
	 * @param collectionPath relative collection path
	 * @throws DatabaseException if collection could not be retrieved or created
	 */
	protected void checkCollection(String collectionPath) throws DatabaseException
	{
		try
		{
			getOrCreateCollection(getBaseUri(), getRelativeRutaCollectionPath() + collectionPath,
					DatabaseAdmin.getInstance().getUsername(), DatabaseAdmin.getInstance().getPassword());
		}
		catch (XMLDBException e)
		{
			throw new DatabaseException("Database connectivity problem. The collection could not be created or retrieved.", e);
		}
	}

	/**
	 * Creates a Collection from an XMLDB URI
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
	 * Stores an xQuery document to the database in collection {@code /system/xquery}.
	 * @param file xQuery file to write
	 * @throws Exception if write failed
	 */
	public static void storeXQueryDocument(File file) throws Exception
	{
		Collection coll = null;
		try
		{
			final String collPath = "/system/xquery";
			coll = getOrCreateCollection(collPath);
			storeDocuments(coll, file);
		}
		finally
		{
			if(coll != null)
			{
				try
				{
					coll.close();
				}
				catch(XMLDBException e){ }
			}
		}
	}

	/**
	 * Stores a File or Directory of Files into a Collection in eXist.
	 * Given a Directory, all files and sub-directories are stored recursively.
	 */
	private static void storeDocuments(final Collection coll, final File source) throws XMLDBException
	{
		if(source.isDirectory())
		{
			final CollectionManagementService mgmtService =
					(CollectionManagementService) coll.getService("CollectionManagementService", "1.0");
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
			//determine the file type
			final MimeTable mimeTable = MimeTable.getInstance();
			final MimeType mimeType = mimeTable.getContentTypeFor(source.getName());
			final String resourceType = mimeType != null && mimeType.isXMLType() ? "XMLResource" : "BinaryResource";

			//store the file
			logger.info("Starting store of {} to {}/{}", source.getAbsolutePath(), coll.getName(), source.getName());
			Resource res = null;
			try
			{
				res = coll.createResource(source.getName(), resourceType);
				res.setContent(source);
				coll.storeResource(res);
				logger.info("Ending store of {} to {}/{}", source.getAbsolutePath(), coll.getName(), source.getName());
			}
			finally
			{
				//cleanup resource
				if(res != null)
					((EXistResource) res).freeResources();
			}
		}
	}

	/**
	 * @return the dbJarPath
	 */
	public static String getDbJarPath()
	{
		return dbJarPath;
	}

	/**
	 * @param dbJarPath the dbJarPath to set
	 */
	public static void setDbJarPath(String dbJarPath)
	{
		ExistConnector.dbJarPath = dbJarPath;
	}

	/**
	 * Gets the path to the query file in the database.
	 * @return {@code String} representing the path
	 */
	public static String getQueryPath()
	{
		return queryPath;
	}

	public static void setQueryPath(String queryPath)
	{
		ExistConnector.queryPath = queryPath;
	}

	/**
	 * Gets the name of the collection that stores deleted documents. The name is prepended with "/".
	 * @return the name of the collection that stores deleted documents prepended with "/".
	 */
	public static String getDeletedPath()
	{
		return deletedPath;
	}

	public static void setDeletedPath(String deletedPath)
	{
		ExistConnector.deletedPath = deletedPath;
	}

	public static String getRelativeRutaCollectionPath()
	{
		return rutaCollectionPath;
	}

	public static void setRelativeRutaPath(String basePath)
	{
		ExistConnector.rutaCollectionPath = basePath;
	}

	public static String getAbsoluteRutaCollectionPath()
	{
		return baseUri + rutaCollectionPath;
	}

	/**
	 * Gets all database accounts' usernames excluding the system ones.
	 * @return list of usernames
	 * @throws DatabaseException due to database connectivity issues
	 */
	public static ArrayList<String> getAccountUsernames() throws DatabaseException
	{
		final ArrayList<String> usernames = new ArrayList<>();
		Collection collection = null;
		try
		{
			collection = getRootCollection();
			final UserManagementService userService = (UserManagementService) collection.getService("UserManagementService", "1.0");
			final Account[] accounts = userService.getAccounts();
			for(final Account account: accounts)
			{
				final String username = account.getName();
				if(!"admin".equals(username) && !"SYSTEM".equals(username) && !"guest".equals(username))
					usernames.add(account.getName());
			}
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("Unable to connect to the database", e);
		}
		finally
		{
			if(collection != null)
				try
				{
					collection.close();
				}
				catch (XMLDBException e)
				{
					logger.error("Unable to close a collection", e);
				}
		}
		return usernames;
	}
}