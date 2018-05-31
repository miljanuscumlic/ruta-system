package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.ObjectFactory;

public class OrderCancellationXmlMapper extends XmlMapper<OrderCancellationType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.ordercancellation_21";
	final private static String collectionPath = "/order-cancellation";

	public OrderCancellationXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<OrderCancellationType> getObjectClass()
	{
		return OrderCancellationType.class;
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
	protected JAXBElement<OrderCancellationType> getJAXBElement(OrderCancellationType object)
	{
		return new ObjectFactory().createOrderCancellation(object);
	}

	@Override
	protected String doPrepareAndGetID(OrderCancellationType orderCancellation, String username, DSTransaction transaction)
			throws DetailException
	{
		return orderCancellation.getUUIDValue();
	}
}