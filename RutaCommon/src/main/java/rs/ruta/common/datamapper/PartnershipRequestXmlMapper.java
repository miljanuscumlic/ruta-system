package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipSearchCriterion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.ObjectFactory;

public class PartnershipRequestXmlMapper extends XmlMapper<PartnershipRequest>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/partnership-request";
	private static final String queryNameSearchPartnership = "search-partnership.xq";

	public PartnershipRequestXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<PartnershipRequest> getObjectClass()
	{
		return PartnershipRequest.class;
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
	protected JAXBElement<PartnershipRequest> getJAXBElement(PartnershipRequest object)
	{
		return new ObjectFactory().createPartnershipRequest(object);
	}

	@Override
	protected String doPrepareAndGetID(PartnershipRequest request, String username, DSTransaction transaction)
			throws DetailException
	{
		return request.getIDValue();
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		String query = null;
		if(criterion.getClass() == PartnershipSearchCriterion.class)
			query = openXmlDocument(getQueryPath(), queryNameSearchPartnership);
		if(query != null)
		{
			final PartnershipSearchCriterion sc = (PartnershipSearchCriterion) criterion;
			final String requesterID = sc.getRequesterPartyID();
			final String requestedID = sc.getRequestedPartyID();
			try
			{
				StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
				queryService.declareVariable("path", queryPath.toString());
				if(requesterID != null)
					queryService.declareVariable("requester-id", requesterID);
				if(requestedID != null)
					queryService.declareVariable("requested-id", requestedID);
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