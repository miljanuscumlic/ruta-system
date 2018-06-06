package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.BusinessPartnershipRequest;
import rs.ruta.common.ObjectFactory;

public class BusinessPartnershipRequestXmlMapper extends XmlMapper<BusinessPartnershipRequest>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/business-partnership-request";

	public BusinessPartnershipRequestXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<BusinessPartnershipRequest> getObjectClass()
	{
		return BusinessPartnershipRequest.class;
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
	protected JAXBElement<BusinessPartnershipRequest> getJAXBElement(BusinessPartnershipRequest object)
	{
		return new ObjectFactory().createBusinessPartnershipRequest(object);
	}

	@Override
	protected String doPrepareAndGetID(BusinessPartnershipRequest request, String username, DSTransaction transaction)
			throws DetailException
	{
		return request.getIDValue();
	}
}