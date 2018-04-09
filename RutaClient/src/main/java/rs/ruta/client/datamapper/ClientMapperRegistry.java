package rs.ruta.client.datamapper;

import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.common.datamapper.PartyIDXmlMapper;
import rs.ruta.common.datamapper.PartyXmlMapper;
import rs.ruta.common.datamapper.UserXmlMapper;
import rs.ruta.common.RutaUser;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.CatalogueSearch;
import rs.ruta.client.Item;
import rs.ruta.client.MyParty;
import rs.ruta.client.PartySearch;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.CreateCatalogueProcess;
import rs.ruta.common.DocBox;
import rs.ruta.common.Followers;
import rs.ruta.common.PartyID;
import rs.ruta.common.datamapper.CatalogueDeletionXmlMapper;
import rs.ruta.common.datamapper.CatalogueXmlMapper;
import rs.ruta.common.datamapper.DataManipulationException;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.DocBoxXmlMapper;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.FollowersXmlMapper;
import rs.ruta.common.datamapper.DatabaseTransactionMapper;
import rs.ruta.common.datamapper.DatabaseTransaction;

public class ClientMapperRegistry extends MapperRegistry
{
	/**
	 * Constructs {@link MapperRegistry} object setting this concrete instace of
	 * {@code ClientMapperRegistry} as a registry. Also, it initializes {@link ExistConnector} object
	 * with {@link LocalExistConnector} instance.
	 */
	public ClientMapperRegistry()
	{
		setRegistry(this);
		/*DatabaseAdmin.newInstance("admin", null);
		ExistConnector connector = new ExistConnector();
		connector.setLocalAPI();
		setConnector(connector);*/

		setConnector(new LocalExistConnector());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S> DataMapper<S, String> getMapper(Class<S> clazz) throws DetailException
	{
		DataMapper<S, String> dataMapper = (DataMapper<S, String>) getInstance().getMapRegistry().get(clazz);
		if(dataMapper == null)
		{
			if(clazz == MyParty.class)
				dataMapper = (DataMapper<S, String>) new MyPartyXmlMapper(getConnector());
			else if(clazz == RutaUser.class)
				dataMapper = (DataMapper<S, String>) new UserXmlMapper(getConnector());
			else if(clazz == DatabaseTransaction.class)
				dataMapper = (DataMapper<S, String>) new DatabaseTransactionMapper(getConnector());
			else if(clazz == PartyType.class)
				dataMapper = (DataMapper<S, String>) new PartyXmlMapper(getConnector());
			else if(clazz == PartyID.class)
				dataMapper = (DataMapper<S, String>) new PartyIDXmlMapper(getConnector());
			else if(clazz == Followers.class)
				dataMapper = (DataMapper<S, String>) new FollowersXmlMapper(getConnector());
			else if(clazz == CatalogueType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueXmlMapper(getConnector());
			else if(clazz == CatalogueDeletionType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueDeletionXmlMapper(getConnector());
			else if(clazz == DocBox.class)
				dataMapper = (DataMapper<S, String>) new DocBoxXmlMapper(getConnector());
			else if(clazz == Item.class)
				dataMapper = (DataMapper<S, String>) new ItemXmlMapper(getConnector());
			else if(clazz == BusinessParty.class)
				dataMapper = (DataMapper<S, String>) new BusinessPartyXmlMapper(getConnector());
			else if(clazz == PartySearch.class)
				dataMapper = (DataMapper<S, String>) new PartySearchXmlMapper(getConnector());
			else if(clazz == CatalogueSearch.class)
				dataMapper = (DataMapper<S, String>) new CatalogueSearchXmlMapper(getConnector());
			else if(clazz == CreateCatalogueProcess.class)
				dataMapper = (DataMapper<S, String>) new CreateCatalogueProcessXmlMapper(getConnector());
			else if(clazz == CatalogueCorrespondence.class)
				dataMapper = (DataMapper<S, String>) new CatalogueCorrespondenceXmlMapper(getConnector());
			if(dataMapper != null)
				getInstance().getMapRegistry().put(clazz, dataMapper);
		}
		if(dataMapper == null)
			throw new DataManipulationException("There is no mapper for the class " + clazz.toString() + ".");
		return dataMapper;
	}
}