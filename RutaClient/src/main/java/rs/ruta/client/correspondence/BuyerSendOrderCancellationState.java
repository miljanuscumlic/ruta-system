package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import rs.ruta.common.DocumentReference;

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
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException(Messages.getString("BuyerSendOrderCancellationState.0")); 
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
				processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("BuyerSendOrderCancellationState.1"))); 
//				changeState(process, BuyerSendOrderCancellationState.getInstance());
			}
		}
		else
			throw new StateActivityException(Messages.getString("BuyerSendOrderCancellationState.2")); 
	}
}