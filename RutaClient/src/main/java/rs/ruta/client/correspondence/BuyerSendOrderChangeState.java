package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;

@XmlRootElement(name = "BuyerSendOrderChangeState")
public class BuyerSendOrderChangeState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerSendOrderChangeState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final OrderChangeType orderChange = process.getOrderChange(correspondence);
		if(orderChange != null)
		{
			DocumentReference documentReference = correspondence.getDocumentReference(orderChange.getUUIDValue());
			process.getClient().cdrSendDocument(orderChange, documentReference, correspondence);
			changeState(process, BuyerReceiveOrderResponseState.getInstance());
		}
		else
		{
//			correspondence.updateDocumentStatus(correspondence.getLastDocumentReference(OrderType.class),
//					DocumentReference.Status.CLIENT_FAILED);
			throw new StateActivityException("Order Change has not been sent to the CDR service! Order Change could not be found!");
		}
	}

}