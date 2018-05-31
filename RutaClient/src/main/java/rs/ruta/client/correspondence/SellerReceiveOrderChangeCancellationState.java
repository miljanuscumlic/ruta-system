package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;

@XmlRootElement(name = "SellerReceiveOrderChangeCancellationState")
public class SellerReceiveOrderChangeCancellationState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerReceiveOrderChangeCancellationState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			correspondence.block();
			final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
			final DocumentReference documentReference = correspondence.getLastDocumentReference();
			if(documentReference.getDocumentTypeValue().equals(OrderChangeType.class.getName()))
			{
				final OrderChangeType orderChange = process.getOrderChange(correspondence);
				correspondence.validateDocument(orderChange);
				changeState(process, SellerChangeOrderState.getInstance());
			}
			else if(documentReference.getDocumentTypeValue().equals(OrderCancellationType.class.getName()))
			{
				final OrderCancellationType orderCancellation = process.getOrderCancellation(correspondence);
				correspondence.validateDocument(orderCancellation);
				changeState(process, SellerCancelOrderState.getInstance());
			}
			else if(documentReference.getDocumentTypeValue().equals(ApplicationResponseType.class.getName()))
			{
				final ApplicationResponseType appResponse = process.getApplicationResponse(correspondence);
				correspondence.validateDocument(appResponse);
				changeState(process, ClosingState.getInstance());
			}
			else
				throw new StateActivityException("Received document of unexpected type.");
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateActivityException("Correspondence has been interrupted!");
		}

	}
}