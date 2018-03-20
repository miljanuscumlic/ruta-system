package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.client.MyParty;
import rs.ruta.client.ObjectFactory;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.XmlMapper;

public class MyPartyXmlMapper extends XmlMapper<MyParty>
{
	final private static String collectionPath = "/my-party";
	final private static String objectPackageName = "rs.ruta.client";

	public MyPartyXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return MyParty.class;
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
	protected JAXBElement<MyParty> getJAXBElement(MyParty object)
	{
		return  new ObjectFactory().createMyParty(object);
	}

}
