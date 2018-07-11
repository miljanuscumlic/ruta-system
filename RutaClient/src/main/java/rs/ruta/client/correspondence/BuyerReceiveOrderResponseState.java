package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "BuyerReceiveOrderResponseState")
public class BuyerReceiveOrderResponseState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerReceiveOrderResponseState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			correspondence.block();

			final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
			final DocumentReference documentReference = correspondence.getLastDocumentReference();
			if(documentReference.getDocumentTypeValue().equals(OrderResponseType.class.getName()))
			{
				final OrderResponseType orderResponse = process.getOrderResponse(correspondence);
				correspondence.validateDocument(orderResponse);
				changeState(process, BuyerProcessOrderResponseState.getInstance());
			}
			else if(documentReference.getDocumentTypeValue().equals(OrderResponseSimpleType.class.getName()))
			{
				final OrderResponseSimpleType orderResponseSimple = process.getOrderResponseSimple(correspondence);
				correspondence.validateDocument(orderResponseSimple);
				changeState(process, BuyerProcessOrderResponseSimpleState.getInstance());
			}
			else
				throw new StateActivityException(Messages.getString("BuyerReceiveOrderResponseState.0")); //$NON-NLS-1$
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped()) //non-intentional interruption
				throw new StateActivityException(Messages.getString("BuyerReceiveOrderResponseState.1")); //$NON-NLS-1$
		}
	}
}