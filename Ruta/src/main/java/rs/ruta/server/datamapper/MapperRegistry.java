package rs.ruta.server.datamapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.BugReport;
import rs.ruta.common.RutaVersion;
import rs.ruta.server.DataManipulationException;
import rs.ruta.server.DetailException;

/**Class that hold global variables accountable for the connection to the classes responsible for the
 * database manipulation. One field is the map containing all <code>DataMapper</code>s that maps domain
 * objects to the structures of the particular data store instace in use. Also, there is a
 * <code>DSTransactionFactory</code> field responsible for the instatiation and controlling of the
 * <code>DSTransaction</code> object.
 *
 */
public final class MapperRegistry
{
	private static MapperRegistry registry = new MapperRegistry();
	private ConcurrentMap<Class<?>, DataMapper<?, String>> mapRegistry;

	private MapperRegistry()
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

	/**Initialize to a new empty MapperRegistry object.
	 */
	public static void initialize()
	{
		registry = new MapperRegistry();
	}

	/**Gets the data mapper for connection to the data store based on the <code>Class</code> paramater.
	 * If mapper for a particular class is not in the registry, it will be added to it prior to its retrieval.
	 * @param clazz <code>Class</code> object of the class which mapper should be returned
	 * @return data mapper for the input parameter object or <code>null</code>
	 * if datamapper for the intended clazz parameter does not exist.
	 * @throws DetailException if mapper could not be created and added to the registry
	 * due to database connetivity issues
	 */
	@SuppressWarnings("unchecked")
	public static <S> DataMapper<S, String> getMapper(Class<S> clazz) throws DetailException
	{
		DataMapper<S, String> dataMapper = (DataMapper<S, String>) getInstance().mapRegistry.get(clazz);
		if(dataMapper == null)
		{
			if(clazz == CatalogueType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueXmlMapper();
			else if(clazz == CatalogueDeletionType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueDeletionXmlMapper();
			else if(clazz == PartyType.class)
				dataMapper = (DataMapper<S, String>) new PartyXmlMapper();
			else if(clazz == User.class)
				dataMapper = (DataMapper<S, String>) new UserXmlMapper();
			else if(clazz == DSTransaction.class)
				dataMapper = (DataMapper<S, String>) new ExistTransactionMapper();
			else if(clazz == PartyID.class)
				dataMapper = (DataMapper<S, String>) new PartyIDXmlMapper();
			else if(clazz == RutaVersion.class)
				dataMapper = (DataMapper<S, String>) new RutaVersionXmlMapper();
			else if(clazz == BugReport.class)
				dataMapper = (DataMapper<S, String>) new BugReportXmlMapper();
			getInstance().mapRegistry.put(clazz, dataMapper);
		}
		if(dataMapper == null)
			throw new DataManipulationException("There is no mapper for the class " + clazz.toString() + ".");
		return dataMapper;
	}

	/**Gets the concrete instance of the <code>DSTransactionFactory</code> in use.
	 * @return
	 */
	public static DSTransactionFactory getTransactionFactory()
	{
		return new ExistTransactionFactory();
	}
}
