package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

@XmlRootElement(name = "SellerReceiveOrderState")
public class SellerReceiveOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerReceiveOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final OrderType order = process.getOrder(correspondence);
		correspondence.validateDocument(order);
		changeState((RutaProcess) correspondence.getState(), SellerProcessOrderState.getInstance());
	}
}