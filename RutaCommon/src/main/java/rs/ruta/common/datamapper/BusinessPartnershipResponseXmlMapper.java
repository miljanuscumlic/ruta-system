package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.BusinessPartnershipResponse;
import rs.ruta.common.ObjectFactory;

public class BusinessPartnershipResponseXmlMapper extends XmlMapper<BusinessPartnershipResponse>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/business-partnership-response";

	public BusinessPartnershipResponseXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<BusinessPartnershipResponse> getObjectClass()
	{
		return BusinessPartnershipResponse.class;
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
	protected JAXBElement<BusinessPartnershipResponse> getJAXBElement(BusinessPartnershipResponse object)
	{
		return new ObjectFactory().createBusinessPartnershipResponse(object);
	}

	@Override
	protected String doPrepareAndGetID(BusinessPartnershipResponse response, String username, DSTransaction transaction)
			throws DetailException
	{
		return response.getIDValue();
	}
}