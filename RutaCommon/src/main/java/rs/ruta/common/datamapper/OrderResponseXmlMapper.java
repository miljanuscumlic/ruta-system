package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.ObjectFactory;

public class OrderResponseXmlMapper extends XmlMapper<OrderResponseType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.orderresponse_21";
	final private static String collectionPath = "/order-response";

	public OrderResponseXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<OrderResponseType> getObjectClass()
	{
		return OrderResponseType.class;
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
	protected JAXBElement<OrderResponseType> getJAXBElement(OrderResponseType object)
	{
		return new ObjectFactory().createOrderResponse(object);
	}

	@Override
	protected String doPrepareAndGetID(OrderResponseType orderResponse, String username, DSTransaction transaction)
			throws DetailException
	{
		return orderResponse.getUUIDValue();
	}

}