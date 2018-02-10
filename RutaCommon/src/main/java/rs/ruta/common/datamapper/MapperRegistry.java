package rs.ruta.common.datamapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.BugReport;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.User;

/**Class that holds global variables accountable for the connection to the classes responsible for the
 * database manipulation. One field is the map containing all <code>DataMapper</code>s that maps domain
 * objects to the structures of the particular data store instace in use. Also, there is a
 * <code>DSTransactionFactory</code> field responsible for the instatiation and controlling of the
 * <code>DSTransaction</code> object.
 */
public abstract class MapperRegistry
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.common");
	private static MapperRegistry registry;
	private ConcurrentMap<Class<?>, DataMapper<?, String>> mapRegistry;
	private static ExistConnector connector;

	protected MapperRegistry()
	{
		mapRegistry = new ConcurrentHashMap<Class<?>, DataMapper<?, String>>();
	}

	/**Gets the singleton mapper registry object.
	 * @return mapper registry object
	 */
	public static MapperRegistry getInstance()
	{
		return registry;
	}

	/**Sets a registry object to a passed concrete instance object of {@link MapperRegistry} subclass.
	 */
	public static void setRegistry(MapperRegistry aRegistry)
	{
		registry = aRegistry;
	}

	protected static ExistConnector getConnector()
	{
		return connector;
	}

	protected static void setConnector(ExistConnector connector)
	{
		MapperRegistry.connector = connector;
	}

	public ConcurrentMap<Class<?>, DataMapper<?, String>> getMapRegistry()
	{
		return mapRegistry;
	}

	public void setMapRegistry(ConcurrentMap<Class<?>, DataMapper<?, String>> mapRegistry)
	{
		this.mapRegistry = mapRegistry;
	}

	/**Gets the {@link DataMapper} for connection to the data store based on the <code>Class</code> paramater.
	 * If mapper for a particular class is not in the registry, it will be added to it prior to its retrieval.
	 * @param clazz <code>Class</code> object of the class which mapper should be returned
	 * @return data mapper for the input parameter object or <code>null</code>
	 * if datamapper for the intended <code>Class</code> parameter does not exist
	 * @throws DetailException if mapper could not be created and added to the registry
	 * due to database connetivity issues
	 */
	public abstract <S> DataMapper<S, String> getMapper(Class<S> clazz) throws DetailException;

	/**Gets the concrete instance of the {@link DSTransactionFactory} in use.
	 * @return concrete {@code DSTransactionFactory} instance object
	 */
	public static DSTransactionFactory getTransactionFactory()
	{
		return new DatabaseTransactionFactory();
	}

	/**Checks whether the datastore is accessible.
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
}
