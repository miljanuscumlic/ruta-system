package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.order_21.ObjectFactory;

public class OrderXmlMapper extends XmlMapper<OrderType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.order_21";
	final private static String collectionPath = "/order";

	public OrderXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<OrderType> getObjectClass()
	{
		return OrderType.class;
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
	protected JAXBElement<OrderType> getJAXBElement(OrderType object)
	{
		return new ObjectFactory().createOrder(object);
	}

}