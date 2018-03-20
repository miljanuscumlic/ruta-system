package rs.ruta.client.datamapper;

import rs.ruta.common.SearchCriterion;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.ObjectFactory;
import rs.ruta.client.PartySearch;
import rs.ruta.client.Search;
import rs.ruta.client.SearchesSearchCriterion;
import rs.ruta.common.datamapper.XmlMapper;

public class PartySearchXmlMapper extends XmlMapper<PartySearch>
{
	private static String objectPackageName = "rs.ruta.client";
	private static String collectionPath = "/search/party";
	private static String queryNameSearchSearches = "search-searches.xq";

	public PartySearchXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return Search.class;
	}

	@Override
	protected String getObjectPackageName()
	{
		return objectPackageName;
	}

	@Override
	protected String getCollectionPath()
	{
		return collectionPath;
	}

	@Override
	protected JAXBElement<PartySearch> getJAXBElement(PartySearch object)
	{
		return new ObjectFactory().createPartySearch(object);
	}

	@Override
	protected String doPrepareAndGetID(PartySearch object, String username, DSTransaction transaction)
	{
		return object.getId();
	}

	//currently not used and should be inspected before it is used!!!
	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		String query = null;
		if(criterion.getClass() == SearchesSearchCriterion.class)
			query = openXmlDocument(getQueryPath(), queryNameSearchSearches);
		if(query != null)
		{
			SearchesSearchCriterion sc = (SearchesSearchCriterion) criterion;
			boolean party = sc.isParty();

			try
			{
				StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
				queryService.declareVariable("path", queryPath.toString());
				if(party)
					queryService.declareVariable("party", true);
			}
			catch(XMLDBException e)
			{
				logger.error(e.getMessage(), e);
				throw new DatabaseException("Could not process the query. There has been an error in the process of its execution.", e);
			}
		}
		return query;
	}
}
