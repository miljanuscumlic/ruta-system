package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ObjectFactory;

public class ApplicationResponseXmlMapper extends XmlMapper<ApplicationResponseType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.applicationresponse_21";
	final private static String collectionPath = "/application-response";

	public ApplicationResponseXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<ApplicationResponseType> getObjectClass()
	{
		return ApplicationResponseType.class;
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
	protected JAXBElement<ApplicationResponseType> getJAXBElement(ApplicationResponseType object)
	{
		return new ObjectFactory().createApplicationResponse(object);
	}

	@Override
	protected String doPrepareAndGetID(ApplicationResponseType appResponse, String username, DSTransaction transaction)
			throws DetailException
	{
		return appResponse.getUUIDValue();
	}

}