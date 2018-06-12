package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.ObjectFactory;

public class PartnershipBreakupXmlMapper extends XmlMapper<PartnershipBreakup>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/partnership-breakup";

	public PartnershipBreakupXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<PartnershipBreakup> getObjectClass()
	{
		return PartnershipBreakup.class;
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
	protected JAXBElement<PartnershipBreakup> getJAXBElement(PartnershipBreakup object)
	{
		return new ObjectFactory().createPartnershipBreakup(object);
	}

	@Override
	protected String doPrepareAndGetID(PartnershipBreakup object, String username, DSTransaction transaction)
			throws DetailException
	{
		return object.getIDValue();
	}
}