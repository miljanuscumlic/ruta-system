package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.ObjectFactory;

public class PartnershipResponseXmlMapper extends XmlMapper<PartnershipResponse>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/partnership-response";

	public PartnershipResponseXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<PartnershipResponse> getObjectClass()
	{
		return PartnershipResponse.class;
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
	protected JAXBElement<PartnershipResponse> getJAXBElement(PartnershipResponse object)
	{
		return new ObjectFactory().createPartnershipResponse(object);
	}

	@Override
	protected String doPrepareAndGetID(PartnershipResponse response, String username, DSTransaction transaction)
			throws DetailException
	{
		return response.getIDValue();
	}
}