package rs.ruta.client.correspondence;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public class BuyerSendOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerSendOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final OrderType order = process.getOrder(correspondence);
		if(order != null)
		{
			DocumentReference documentReference = correspondence.getDocumentReference(order.getUUIDValue());
			process.getClient().cdrSendDocument(order, documentReference, correspondence);
			changeState(process, BuyerReceiveOrderResponseState.getInstance());
		}
		else
		{
//			correspondence.updateDocumentStatus(correspondence.getLastDocumentReference(OrderType.class),
//					DocumentReference.Status.CLIENT_FAILED);
			throw new StateActivityException("Order has not been sent to the CDR service! Order could not be found!");
		}
	}
}