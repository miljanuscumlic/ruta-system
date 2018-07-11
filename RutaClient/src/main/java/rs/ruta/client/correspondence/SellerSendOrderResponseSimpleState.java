package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AcceptedIndicatorType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "SellerSendOrderResponseSimpleState")
public class SellerSendOrderResponseSimpleState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerSendOrderResponseSimpleState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final OrderResponseSimpleType orderResponseSimple = process.getOrderResponseSimple(correspondence);
		if(orderResponseSimple != null)
		{
			final DocumentReference documentReference = correspondence.getDocumentReference(orderResponseSimple.getUUIDValue());
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException(Messages.getString("SellerSendOrderResponseSimpleState.0")); 
					//flags are not persisted so set them if the correspondence was stored to the database in its blocked state
					if(!process.isOrderAccepted() && !process.isOrderRejected())
					{
						final AcceptedIndicatorType acceptedIndicator = orderResponseSimple.getAcceptedIndicator();
						if(acceptedIndicator != null)
						{
							if(acceptedIndicator.isValue())
								process.setOrderAccepted(true);
							else
								process.setOrderRejected(true);
						}
						else
							throw new StateActivityException(Messages.getString("SellerSendOrderResponseSimpleState.1")); 
					}
				}
			}
			try
			{
				process.getClient().cdrSendDocument(orderResponseSimple, documentReference, correspondence);
				if(process.isOrderAccepted() && !process.isOrderRejected())
				{
					process.setOrderAccepted(false);
					changeState(process, SellerReceiveOrderChangeCancellationState.getInstance());
				}
				else if(process.isOrderRejected() && !process.isOrderAccepted())
					changeState(process, ClosingState.getInstance());
				else
					throw new StateActivityException(Messages.getString("SellerSendOrderResponseSimpleState.2")); 
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("SellerSendOrderResponseSimpleState.3"))); 
//				changeState(process, SellerSendOrderResponseSimpleState.getInstance());
			}
		}
		else
			throw new StateActivityException(Messages.getString("SellerSendOrderResponseSimpleState.4")); 
	}
}