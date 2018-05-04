package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.ObjectFactory;

public class DeregistrationNoticeXmlMapper extends XmlMapper<DeregistrationNotice>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/deregistration-notice";

	public DeregistrationNoticeXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<DeregistrationNotice> getObjectClass()
	{
		return DeregistrationNotice.class;
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
	protected JAXBElement<DeregistrationNotice> getJAXBElement(DeregistrationNotice object)
	{
		return new ObjectFactory().createDeregistrationNotice(object);
	}

}