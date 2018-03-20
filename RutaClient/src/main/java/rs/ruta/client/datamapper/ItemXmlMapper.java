package rs.ruta.client.datamapper;

import java.util.List;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.client.Item;
import rs.ruta.client.ObjectFactory;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.XmlMapper;

public class ItemXmlMapper extends XmlMapper<Item>
{
	private static String objectPackageName = "rs.ruta.client";
	private static String collectionPath = "/item";
	public ItemXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<Item> getObjectClass()
	{
		return Item.class;
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
	protected JAXBElement<Item> getJAXBElement(Item object)
	{
		return  new ObjectFactory().createItem(object);
	}

	@Override
	protected String doPrepareAndGetID(Item item, String username, DSTransaction transaction)
	{
		//MMM: this ID will be changed
//		String id = InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getID(), IDType::getValue);
		String id = null;
		try
		{
			id = InstanceFactory.getPropertyOrNull(item.getID(), IDType::getValue);
		}
		catch(Exception e) { } // OK, null will be returned
		return id;
	}

	@Override
	public void insertAll(String username, List<Item> list) throws DetailException
	{
		super.insertAll(username, list);
	}

}