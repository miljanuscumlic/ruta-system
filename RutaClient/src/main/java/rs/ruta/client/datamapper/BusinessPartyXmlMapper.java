package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.client.BusinessParty;
import rs.ruta.client.ObjectFactory;
import rs.ruta.common.BusinessPartySearchCriterion;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.XmlMapper;

public class BusinessPartyXmlMapper extends XmlMapper<BusinessParty>
{
	private static String objectPackageName = "rs.ruta.client";
	private static String collectionPath = "/business-party";
	private static String queryNameSearchBusinessParty = "search-business-party.xq";
	public BusinessPartyXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return BusinessParty.class;
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
	protected JAXBElement<BusinessParty> getJAXBElement(BusinessParty object)
	{
		return new ObjectFactory().createBusinessParty(object);
	}

	@Override
	protected String doPrepareAndGetID(BusinessParty object, String username, DSTransaction transaction)
	{
		return object.getCoreParty().getPartyID();
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		String query = null;
		if(criterion.getClass() == BusinessPartySearchCriterion.class)
			query = openXmlDocument(getQueryPath(), queryNameSearchBusinessParty);
		if(query != null)
		{
			BusinessPartySearchCriterion sc = (BusinessPartySearchCriterion) criterion;
			boolean following = sc.isFollowing();
			boolean partner = sc.isPartner();
			boolean other = sc.isOther();
			boolean archived = sc.isArchived();
			boolean deregistered = sc.isDeregistered();

			try
			{
				StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
				queryService.declareVariable("path", queryPath.toString());
				if(following)
					queryService.declareVariable("following", true);
				if(partner)
					queryService.declareVariable("partner", true);
				if(other)
					queryService.declareVariable("other", true);
				if(archived)
					queryService.declareVariable("archived", true);
				if(deregistered)
					queryService.declareVariable("deregistered", true);
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