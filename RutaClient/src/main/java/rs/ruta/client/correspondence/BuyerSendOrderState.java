package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "BuyerSendOrderState")
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
			final DocumentReference documentReference = correspondence.getDocumentReference(order.getUUIDValue());
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException(Messages.getString("BuyerSendOrderState.0")); 
				}
			}
			try
			{
				process.getClient().cdrSendDocument(order, documentReference, correspondence);
				changeState(process, BuyerReceiveOrderResponseState.getInstance());
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("BuyerSendOrderState.1"))); 
//				changeState(process, BuyerSendOrderState.getInstance());
			}
		}
		else
		{
//			correspondence.updateDocumentStatus(correspondence.getLastDocumentReference(OrderType.class),
//					DocumentReference.Status.CLIENT_FAILED);
			throw new StateActivityException(Messages.getString("BuyerSendOrderState.2")); 
		}
	}
}