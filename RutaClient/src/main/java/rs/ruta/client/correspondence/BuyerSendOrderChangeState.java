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
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException(Messages.getString("BuyerSendOrderChangeState.0")); 
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
				processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("BuyerSendOrderChangeState.1"))); 
//				changeState(process, BuyerSendOrderChangeState.getInstance());
			}
		}
		else
			throw new StateActivityException(Messages.getString("BuyerSendOrderChangeState.2")); 
	}
}