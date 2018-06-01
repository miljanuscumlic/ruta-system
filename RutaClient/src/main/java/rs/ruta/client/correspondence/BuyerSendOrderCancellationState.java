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
			final DocumentReference documentReference = correspondence.getDocumentReference(orderCancellation.getUUIDValue());
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous atempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException("Correspondence has been interrupted!");
				}
			}
			try
			{
				process.getClient().cdrSendDocument(orderCancellation, documentReference, correspondence);
				changeState(process, ClosingState.getInstance());
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder("Sending Order Cancellation has failed!"));
				changeState(process, BuyerSendOrderCancellationState.getInstance());
			}
		}
		else
			throw new StateActivityException("Order Cancellation has not been sent to the CDR service! Order Cancellation could not be found!");
	}
}