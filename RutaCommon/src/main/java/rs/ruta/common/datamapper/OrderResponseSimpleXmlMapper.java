package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.ObjectFactory;

public class OrderResponseSimpleXmlMapper extends XmlMapper<OrderResponseSimpleType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21";
	final private static String collectionPath = "/order-response-simple";

	public OrderResponseSimpleXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<OrderResponseSimpleType> getObjectClass()
	{
		return OrderResponseSimpleType.class;
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
	protected JAXBElement<OrderResponseSimpleType> getJAXBElement(OrderResponseSimpleType object)
	{
		return new ObjectFactory().createOrderResponseSimple(object);
	}

	@Override
	protected String doPrepareAndGetID(OrderResponseSimpleType orderResponse, String username, DSTransaction transaction)
			throws DetailException
	{
		return orderResponse.getUUIDValue();
	}

}