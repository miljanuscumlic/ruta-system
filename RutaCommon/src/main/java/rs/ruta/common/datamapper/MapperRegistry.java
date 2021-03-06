package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.BugReport;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.RutaUser;

/**
 * Abstract class that holds global variables accountable for the connection to the classes responsible for the
 * data store manipulation. One field of a class is the map containing all {@link DataMapper}s that map domain
 * objects to the structures of the particular data store instance in use. Also, there is a
 * {@link DSTransactionFactory} field responsible for the instantiation and control of the
 * {@link DSTransaction} object.
 */
public abstract class MapperRegistry
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.common");
	private static MapperRegistry registry;
	private ConcurrentMap<Class<?>, DataMapper<?, String>> mapRegistry;
	private static DatastoreConnector connector;

	protected MapperRegistry()
	{
		mapRegistry = new ConcurrentHashMap<Class<?>, DataMapper<?, String>>();
	}

	/**
	 * Initializes {@code MapperRegistry}.
	 * @param factory factory class that intantiates proper mapper object
	 * @return initialized {@code MapperRegistry}
	 */
	public static MapperRegistry initialize(AbstractMapperRegistryFactory factory)
	{
		setInstance(factory.createMapperRegistry());
		setConnector(factory.createExistConnector());
		return getInstance();
	}

	/**
	 * Gets the singleton mapper registry object.
	 * @return mapper registry object
	 */
	public static MapperRegistry getInstance()
	{
		return registry;
	}

	/**
	 * Sets a passed concrete instance object of {@link MapperRegistry} subclass as a singleton registry object.
	 */
	public static void setInstance(MapperRegistry registry)
	{
		MapperRegistry.registry = registry;
	}

	/**
	 * Gets the {@link DatastoreConnector connector} object to the datastore.
	 * @return
	 */
	public static DatastoreConnector getConnector()
	{
		return connector;
	}

	/**
	 * Sets the {@link ExistConnector connector} object to the datastore.
	 * @param connector connector to set
	 */
	protected static void setConnector(ExistConnector connector)
	{
		MapperRegistry.connector = connector;
	}

	/**
	 * Gets the map of registered {@link DataMapper}s.
	 * @return map of {@code DataMapper}s
	 */
	public ConcurrentMap<Class<?>, DataMapper<?, String>> getMapRegistry()
	{
		return mapRegistry;
	}

	/**
	 * Gets the {@link DataMapper} for connection to the data store based on the <code>Class</code> paramater.
	 * If mapper for a particular class is not in the registry, it will be added to it prior to its retrieval.
	 * @param clazz <code>Class</code> object of the class which mapper should be returned
	 * @return data mapper for the input parameter object or <code>null</code>
	 * if datamapper for the intended <code>Class</code> parameter does not exist
	 * @throws DetailException if mapper could not be created and added to the registry
	 * due to database connetivity issues
	 */
	public abstract <S> DataMapper<S, String> getMapper(Class<S> clazz) throws DetailException;

	/**
	 * Gets the concrete instance of the {@link DSTransactionFactory} in use.
	 * @return concrete {@code DSTransactionFactory} instance object
	 */
	public static DSTransactionFactory getTransactionFactory()
	{
		return new DatabaseTransactionFactory();
	}

	/**
	 * Checks whether the datastore is accessible.
	 * @return true or false
	 */
	public static boolean isDatastoreAccessible()
	{
		try
		{
			//tries to connect to the database because maybe it was not tried before
			connector.connectToDatabase();
			return true;
		}
		catch (DatabaseException e)
		{
			logger.info("Database is not accessible. Exception is ", e);
			return false;
		}
	}

	/**
	 * Clears cache i.e. in-memory objects in all registered datamappers.
	 */
	public void clearCachedObjects()
	{
		getInstance().getMapRegistry().forEach((clazz, datamapper) ->
		{
			datamapper.clearCache();
		});
	}

	/**
	 * Gets all datastore accounts' usernames.
	 * @return list of usernames
	 * @throws DatabaseException due to database connectivity issues
	 */
	public static List<String> getAccountUsernames() throws DatabaseException
	{
		ArrayList<String> usernames = new ArrayList<>();
		usernames = ExistConnector.getAccountUsernames();
		return usernames;
	}

}
