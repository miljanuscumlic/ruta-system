package rs.ruta.common.datamapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;

import org.exist.xmldb.DatabaseInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**ExistConnector encapsulates common data and methods related to connection to the eXist database.
 */
public class ExistConnector implements DatastoreConnector
{
	private static String databaseId = "exist";
	private static String uriPrefix = "xmldb:" + databaseId + "://";
	private static String rutaDevelopCollectionPath = "/db/ruta-develop"; // path of the application's base collection in develop branch
	private static String rutaMasterCollectionPath = "/db/ruta"; // path of the application's base collection in master branch
	private static String rutaCollectionPath = rutaDevelopCollectionPath; // path of the application's base collection
	private static String uriSufix = "/exist/xmlrpc";
	private static String server = "localhost";
	private static String port = "8888";
	private static String baseUri = uriPrefix + server + ":" + port + uriSufix;
	private static String dbJarPath = "C:\\Programs\\exist-db\\start.jar"; // path to the database jar archive
	private static String docSufix = ".xml";
	private static String deletedPath = "/deleted";
	private static String queryPath = "/system/query";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.server.datamapper");
	private static boolean connected = false;

	/**Constructs eXist database instance and registers it at the <code>DatabaseManager</code>, enabling
	 * the application to communicate with it.
	 */
	public static void connectToDatabase() throws DatabaseException
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
		if(isDatabaseAccessible())
			connected = true;
		else
			throw new DatabaseException("Database connectivity issue. Database is not accessible.");
	}

	/**Checks whether the database is accessible.
	 * @return true or false
	 */
	static boolean isDatabaseAccessible()
	{
		boolean access = true;
		try
		{
			getRootCollection();
		}
		catch (XMLDBException e)
		{
			access = false;
			//logger.error("Exception is ", e);
		}
		return access;
	}

	/**Starts the eXist database application in its own jetty server on this machine.
	 * @throws Exception if eXist could not be started
	 */
	public static void startDatabase() throws Exception
	{
		new Thread( () ->
		{
			Process process;
			try
			{
				logger.info("User directory: " + System.getProperty("user.dir"));
				logger.info("User home: " + System.getProperty("user.home"));

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
				logger.error("Exception is " , e);
				//throw e;
			}
		}).start();
	}

	/**Sends <code>Inpustream</code> text to the console window.
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

	/**Shuts down the eXist database, its application program and jetty server as its container.
	 * @throws Exception if database could not be stopped
	 */
	public static void shutdownDatabase() throws Exception
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
			logger.error("Exception is ", e);
			throw e;
		}
	}

	/**Restarts the eXist database and its jetty container.
	 * @throws Exception if eXist cannot be restarted
	 */
	public static void restartDatabase() throws Exception
	{
		shutdownDatabase();
		startDatabase();
	}

	/**Retrieves the URI prefix of the eXist database server
	 * @return the uriPrefix
	 */
	public static String getUriPrefix()
	{
		return uriPrefix;
	}
	/**Sets the URI prefix of the eXist database server
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

	/**Sets the IP of the eXist database server
	 * @param server the server to set
	 */
	public static void setServer(String server)
	{
		ExistConnector.server = server;
	}

	/**Retrieves the port of the eXist database server
	 * @return the port
	 */
	public static String getPort()
	{
		return port;
	}

	/**Sets the port of the eXist database server
	 * @param port the port to set
	 */
	public static void setPort(String port)
	{
		ExistConnector.port = port;
	}

	/**Returns base part of the URI of the eXist database.
	 * @return the uri of the eXist database
	 */
	public static String getBaseUri()
	{
		return baseUri;
	}

	/**Sets static fields so that the local XMLDB API is called.
	 */
	public void setLocalAPI()
	{
		baseUri = uriPrefix + "/";
	}

	public static String getDocumentSufix() { return docSufix; }

	public static void setDocumentSufix(String docSufix) { ExistConnector.docSufix = docSufix; }

	/**Gets the root collection of the database as database admin.
	 * @return root Collection instance or <code>null</code> if the collection could not be found.
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getRootCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(baseUri + rutaCollectionPath, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the root collection from the database as a specified user. Collection that is retrieved
	 * is defined in the subclasses of the XmlMapper.
	 * @return root Collection instance or <code>null</code> if the collection could not be found.
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public static Collection getRootCollection(String username, String password) throws XMLDBException
	{
		return DatabaseManager.getCollection(baseUri + rutaCollectionPath, username, password);
	}

	/**Gets the collection from the database as a database admin. If collection does not exist, method creates it.
	 * @param collectionPath relative path to the collection
	 * @return a <code>Collection</code> instance for the requested collection
	 * @throws XMLDBException if the collection could not be retrieved or created
	 */
	public static Collection getOrCreateCollection(String collectionPath) throws XMLDBException
	{
		return getOrCreateCollection(getBaseUri(), getRelativeRutaCollectionPath() + collectionPath,
				DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the collection from the database. If collection does not exist, method creates it. The user
	 * credentials on whose behalf collection is retrieved are passed as parameters.
	 * @param uri absolute URI defining the base collection on which the subcollection relative path is appended
	 * @param collectionUri relative path of the subcollection that is acctualy retrived
	 * @param username user's username of whose befalf collection is retrieved
	 * @param password user's password
	 * @return a <code>Collection</code> instance for the requested collection
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

	/**Creates all subcollections of the passed <code>Collection</code> argument.
	 * @param current <code>Collection</code> which subcollections are created
	 * @param descendants all subcollections that should be created or traversed on the path
	 * @return a <code>Collection</code> instance for the passed collection
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

	/**Checks if the collection exist. If not, method creates the collection with passed user credentials as an owner of the collection.
	 * @param collectionPath relative collection path
	 * @param username user's username
	 * @param password user's password
	 * @throws XMLDBException if collection could not be created or retrieved
	 */
	protected static void checkCollection(String collectionPath, String username, String password) throws XMLDBException
	{
		getOrCreateCollection(getBaseUri(), getRelativeRutaCollectionPath() + collectionPath, username, password);
	}

	/**Checks whether the collection exist. If not, method creates the collection and asigns database admin
	 * as an owner of the collection.
	 * @param collectionPath relative collection path
	 * @throws DatabaseException if collection could not be retrieved or created
	 */
	protected static void checkCollection(String collectionPath) throws DatabaseException
	{
		try
		{
			getOrCreateCollection(getBaseUri(), getRelativeRutaCollectionPath() + collectionPath, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
		}
		catch (XMLDBException e)
		{
			throw new DatabaseException("Database connectivity problem. The collection could not be created or retrieved."/*e.getMessage()*/ , e);
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

	public static String getQueryPath()
	{
		return queryPath;
	}

	public static void setQueryPath(String queryPath)
	{
		ExistConnector.queryPath = queryPath;
	}

	/**Gets the name of the collection that stores deleted documents. The name is prepended with "/".
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

}
