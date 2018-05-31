package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;

@XmlRootElement(name = "BuyerSendOrderCancellationState")
public class BuyerSendOrderCancellationState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerSendOrderCancellationState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final OrderCancellationType orderCancellation = process.getOrderCancellation(correspondence);
		if(orderCancellation != null)
		{
			DocumentReference documentReference = correspondence.getDocumentReference(orderCancellation.getUUIDValue());
			process.getClient().cdrSendDocument(orderCancellation, documentReference, correspondence);
			changeState(process, ClosingState.getInstance());
		}
		else
		{
//			correspondence.updateDocumentStatus(correspondence.getLastDocumentReference(OrderType.class),
//					DocumentReference.Status.CLIENT_FAILED);
			throw new StateActivityException("Order Cancellation has not been sent to the CDR service! Order Cancellation could not be found!");
		}
	}

}