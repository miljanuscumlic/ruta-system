package rs.ruta.server.datamapper;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.BugReport;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.Followers;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.User;
import rs.ruta.common.datamapper.BugReportXmlMapper;
import rs.ruta.common.datamapper.CatalogueDeletionXmlMapper;
import rs.ruta.common.datamapper.CatalogueXmlMapper;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DataManipulationException;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.DocumentDistributionXmlMapper;
import rs.ruta.common.datamapper.DocumentTransaction;
import rs.ruta.common.datamapper.DocumentTransactionMapper;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.ExistTransactionMapper;
import rs.ruta.common.datamapper.FollowersXmlMapper;
import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.common.datamapper.PartyIDXmlMapper;
import rs.ruta.common.datamapper.PartyXmlMapper;
import rs.ruta.common.datamapper.RutaVersionXmlMapper;
import rs.ruta.common.datamapper.UserXmlMapper;

/**Class that holds global variables accountable for the connection to the classes responsible for the
 * database manipulation. One field is the map containing all <code>DataMapper</code>s that maps domain
 * objects to the structures of the particular data store instace in use. Also, there is a
 * <code>DSTransactionFactory</code> field responsible for the instatiation and controlling of the
 * <code>DSTransaction</code> object.
 */
public class ServiceMapperRegistry extends MapperRegistry
{
	/**Constructs {@link MapperRegistry} object setting this concrete instace of
	 * {@code ServiceMapperRegistry} as a registry. Also, it sets {@link ExistConnector} object.
	 */
	public ServiceMapperRegistry()
	{
		setRegistry(this);
		setConnector(new RemoteExistConnector());
	}

	/**Gets the {@link DataMapper} for connection to the data store based on the <code>Class</code> paramater.
	 * If mapper for a particular class is not in the registry, it will be added to it prior to its retrieval.
	 * @param clazz <code>Class</code> object of the class which mapper should be returned
	 * @return data mapper for the input parameter object or <code>null</code>
	 * if datamapper for the intended <code>Class</code> parameter does not exist
	 * @throws DetailException if mapper could not be created and added to the registry
	 * due to database connetivity issues
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <S> DataMapper<S, String> getMapper(Class<S> clazz) throws DetailException
	{
		DataMapper<S, String> dataMapper = (DataMapper<S, String>) getInstance().getMapRegistry().get(clazz);
		if(dataMapper == null)
		{
			if(clazz == CatalogueType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueXmlMapper(getConnector());
			else if(clazz == CatalogueDeletionType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueDeletionXmlMapper(getConnector());
			else if(clazz == PartyType.class)
				dataMapper = (DataMapper<S, String>) new PartyXmlMapper(getConnector());
			else if(clazz == User.class)
				dataMapper = (DataMapper<S, String>) new UserXmlMapper(getConnector());
			else if(clazz == DSTransaction.class)
				dataMapper = (DataMapper<S, String>) new ExistTransactionMapper(getConnector());
			else if(clazz == DocumentTransaction.class)
				dataMapper = (DataMapper<S, String>) new DocumentTransactionMapper(getConnector());
			else if(clazz == PartyID.class)
				dataMapper = (DataMapper<S, String>) new PartyIDXmlMapper(getConnector());
			else if(clazz == RutaVersion.class)
				dataMapper = (DataMapper<S, String>) new RutaVersionXmlMapper(getConnector());
			else if(clazz == BugReport.class)
				dataMapper = (DataMapper<S, String>) new BugReportXmlMapper(getConnector());
			else if(clazz == Followers.class)
				dataMapper = (DataMapper<S, String>) new FollowersXmlMapper(getConnector());
			else if(clazz == DocumentDistribution.class)
				dataMapper = (DataMapper<S, String>) new DocumentDistributionXmlMapper(getConnector());
			if(dataMapper != null)
				getInstance().getMapRegistry().put(clazz, dataMapper);
		}
		if(dataMapper == null)
			throw new DataManipulationException("There is no mapper for the class " + clazz.toString() + ".");
		return dataMapper;
	}
}
