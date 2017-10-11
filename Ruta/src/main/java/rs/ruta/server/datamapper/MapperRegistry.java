package rs.ruta.server.datamapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.server.DataManipulationException;
import rs.ruta.server.DetailException;

/**Class that hold global variables accountable for the connection to the classes responsible for the
 * database manipulation. One filed is the map containing all <code>DataMapper</code>s that maps domain
 * objects to the structeres of the particular data store instace in use. Also, there is a
 * <code>DSTransactionFactory</code> field responsible for the instatiation and controlling of the
 * <code>DSTransaction</code> object.
 *
 */
public final class MapperRegistry
{
	private static MapperRegistry registry = new MapperRegistry();
	private ConcurrentMap<Class<?>, DataMapper> mapRegistry;

	private MapperRegistry()
	{
		mapRegistry = new ConcurrentHashMap<Class<?>, DataMapper>();
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
	public static DataMapper getMapper(Class<?> clazz) throws DetailException
	{
		DataMapper dataMapper = getInstance().mapRegistry.get(clazz);
		if(dataMapper == null)
		{
			if(clazz == CatalogueType.class)
				dataMapper = new CatalogueXmlMapper();
			else if(clazz == CatalogueDeletionType.class)
				dataMapper = new CatalogueDeletionXmlMapper();
			else if(clazz == PartyType.class)
				dataMapper = new PartyXmlMapper();
			else if(clazz == User.class)
				dataMapper = new UserXmlMapper();
			else if(clazz == DSTransaction.class)
				dataMapper = new ExistTransactionMapper();
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
