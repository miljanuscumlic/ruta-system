package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.client.MyParty;
import rs.ruta.client.gui.OrderResponseDialog;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "SellerSendOrderResponseState")
public class SellerSendOrderResponseState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerSendOrderResponseState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final OrderResponseType orderResponse = process.getOrderResponse(correspondence);
		if(orderResponse != null)
		{
			final DocumentReference documentReference = correspondence.getDocumentReference(orderResponse.getUUIDValue());
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
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
				process.getClient().cdrSendDocument(orderResponse, documentReference, correspondence);
				process.setOrderModified(false);
				changeState(process, SellerReceiveOrderChangeCancellationState.getInstance());
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder("Sending Order Response has failed!")); 
//				changeState(process, SellerSendOrderResponseState.getInstance());
			}
		}
		else
			throw new StateActivityException("Order Response has not been sent to the CDR service! Order Response could not be found!"); 
	}
}