package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.ObjectFactory;;

public class OrderChangeXmlMapper extends XmlMapper<OrderChangeType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.orderchange_21";
	final private static String collectionPath = "/order-change";

	public OrderChangeXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<OrderChangeType> getObjectClass()
	{
		return OrderChangeType.class;
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
	protected JAXBElement<OrderChangeType> getJAXBElement(OrderChangeType object)
	{
		return new ObjectFactory().createOrderChange(object);
	}

	@Override
	protected String doPrepareAndGetID(OrderChangeType orderCancellation, String username, DSTransaction transaction)
			throws DetailException
	{
		return orderCancellation.getUUIDValue();
	}
}