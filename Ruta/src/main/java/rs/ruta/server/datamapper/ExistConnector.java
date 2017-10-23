package rs.ruta.server.datamapper;

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

import rs.ruta.server.DatabaseException;

/**ExistConnector encapsulates common data and methods related to connection to the eXist database.
 */
public class ExistConnector implements DatastoreConnector
{
	private static String uriPrefix = "xmldb:exist://";
	private static String uriSufix = "/exist/xmlrpc/db";
	private static String server = "localhost";
	private static String port = "8888";
	private static String uri = uriPrefix + server + ":" + port + uriSufix;
	private static String dbJarPath = "C:\\Programs\\exist-db\\start.jar"; // path to the database jar archive
	private static String docSufix = ".xml";
	private static String transactionPath = "/ruta/system/transactions";
	private static String queryPath ="/ruta/system/queries";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.server.datamapper");
	protected static final String queryNameSearchCatalogue = "search-catalogue.xq";
	protected static final String queryNameSearchParty = "search-party.xq";

	/**Constructs eXist database instance and registers it at the <code>DatabaseManager</code>, enabling
	 * the application to communicate with it.
	 */
	public static void connectDatabase()
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
			logger.error("Exception is: ", e);
		}
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
				e.printStackTrace();
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
		}
		catch (XMLDBException e)
		{
			logger.error("Exception is: ", e);
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

	/**Returns URI of the eXist database
	 * @return the uri of the eXist database
	 */
	public static String getUri()
	{
		return uri;
	}

	public String getDocumentSufix() { return docSufix; }

	public void setDocumentSufix(String docSufix) { ExistConnector.docSufix = docSufix; }

	/**Gets the root collection of the database as database admin.
	 * @return root collection object
	 * @throws XMLDBException if the collection cannot be retrieved
	 */
	public static Collection getRootCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(uri, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the root collection from the database as a specified user. Collection that is retrieved
	 * is defined in the subclasses of the XmlMapper.
	 * @return requested collection object
	 * @throws XMLDBException if the collection cannot be retrieved
	 */
	public Collection getRootCollection(String username, String password) throws XMLDBException
	{
		return DatabaseManager.getCollection(uri, username, password);
	}
	/**Gets the path of the collection in which are stored transaction related documents.
	 * @return the transactionPath collection path to the /transactions collection
	 */
	public static String getTransactionPath()
	{
		return transactionPath;
	}
	/**Sets the path of the collection in which are stored transaction related documents.
	 * @param transactionPath the transactionPath to set
	 */
	public static void setTransactionPath(String transactionPath)
	{
		ExistConnector.transactionPath = transactionPath;
	}

	protected static Collection getOrCreateCollection(final String uri, final String collectionUri,
			final String username, final String password) throws XMLDBException
	{
		final Queue<String> segments = new ArrayDeque<String>();
		for(final String pathSegment : collectionUri.split("/"))
			if(!pathSegment.isEmpty())
				segments.add(pathSegment);
		Collection current = DatabaseManager.getCollection(uri + "/" + segments.poll(), username, password);
		if(current == null) // root collection (/db) does not exist and can not be created
			throw new XMLDBException();
		return getOrCreateCollection(current, segments);
	}

	private static Collection getOrCreateCollection(final Collection current, final Queue<String> descendants) throws XMLDBException
	{
		if(descendants.isEmpty())
			return current;
		else
		{
			final String childName = descendants.poll();
			Collection child = current.getChildCollection(childName);
			if(child == null)
			{
				final CollectionManagementService mgmt =
						(CollectionManagementService) current.getService("CollectionManagementService", "1.0");
				child = mgmt.createCollection(childName);
				current.close(); //close the current collection, child will remain open
			}
			return getOrCreateCollection(child, descendants);
		}
	}

	/**Checks if the collection exist. If not, method creates the collection with passed user credentials as an owner of the collection.
	 * @param collectionPath relative collection path
	 * @param username user's username
	 * @param password user's password
	 * @throws XMLDBException if collection cannot be created or retrieved
	 */
	protected static void checkCollection(String collectionPath, String username, String password) throws XMLDBException
	{
		getOrCreateCollection(getUri(), collectionPath, username, password);
	}

	/**Checks if the collection exist. If not, method creates the collection and asigns database admin as an owner of the collection.
	 * @param collectionPath relative collection path
	 * @throws XMLDBException if collection cannot be created or retrieved
	 * @throws DatabaseException
	 */
	protected static void checkCollection(String collectionPath) throws DatabaseException
	{
		try
		{
			getOrCreateCollection(getUri(), collectionPath, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
		}
		catch (XMLDBException e)
		{
			throw new DatabaseException("Database connectivity problem. The collection could not be created or retrieved.");
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

}
