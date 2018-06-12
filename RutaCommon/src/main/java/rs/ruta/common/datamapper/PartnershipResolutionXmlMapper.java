package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.PartnershipResolution;
import rs.ruta.common.ObjectFactory;

public class PartnershipResolutionXmlMapper extends XmlMapper<PartnershipResolution>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/partnership-resolution";

	public PartnershipResolutionXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<PartnershipResolution> getObjectClass()
	{
		return PartnershipResolution.class;
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
	protected JAXBElement<PartnershipResolution> getJAXBElement(PartnershipResolution object)
	{
		return new ObjectFactory().createPartnershipResolution(object);
	}

	@Override
	protected String doPrepareAndGetID(PartnershipResolution object, String username, DSTransaction transaction)
			throws DetailException
	{
		return object.getIDValue();
	}
}