package rs.ruta.client.datamapper;

import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.common.datamapper.PartyXmlMapper;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.BugReport;
import rs.ruta.common.datamapper.BugReportXmlMapper;
import rs.ruta.common.datamapper.CatalogueDeletionXmlMapper;
import rs.ruta.common.datamapper.CatalogueXmlMapper;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DataManipulationException;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.ExistTransactionMapper;

public class ClientMapperRegistry extends MapperRegistry
{
	public ClientMapperRegistry()
	{
		initialize(this);
		ExistConnector connector = new ExistConnector();
		connector.setLocalAPI();
		setConnector(connector);
	}

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
			else if(clazz == DSTransaction.class)
				dataMapper = (DataMapper<S, String>) new ExistTransactionMapper(getConnector());
			else if(clazz == BugReport.class)
				dataMapper = (DataMapper<S, String>) new BugReportXmlMapper(getConnector());
			if(dataMapper != null)
				getInstance().getMapRegistry().put(clazz, dataMapper);
		}
		if(dataMapper == null)
			throw new DataManipulationException("There is no mapper for the class " + clazz.toString() + ".");
		return dataMapper;
	}
}