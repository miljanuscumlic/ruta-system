package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import rs.ruta.common.DocumentReference;

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
			final DocumentReference documentReference = correspondence.getDocumentReference(orderChange.getUUIDValue());
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
				process.getClient().cdrSendDocument(orderChange, documentReference, correspondence);
				process.setOrderChanged(false);
				changeState(process, BuyerReceiveOrderResponseState.getInstance());
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder("Sending Order Change has failed!"));
				changeState(process, BuyerSendOrderChangeState.getInstance());
			}
		}
		else
			throw new StateActivityException("Order Change has not been sent to the CDR service! Order Change could not be found!");
	}
}