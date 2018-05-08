package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;

@XmlRootElement(name = "BuyerProcessResponseState")
public class BuyerProcessResponseState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerProcessResponseState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveOrderResponse(final RutaProcess process)
	{
		//MMM to implement
		changeState(process, BuyerChangeOrderState.getInstance());
		changeState(process, BuyerCancelOrderState.getInstance());
		changeState(process, BuyerAcceptOrderState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		OrderResponseSimpleType orderResponseSimple;
		OrderType order;
		final int docCount = correspondence.getDocumentReferenceCount();
		if(docCount > 0)
		{
			final DocumentReference docReference = correspondence.getDocumentReferenceAtIndex(docCount - 1);
			final String docType = docReference.getDocumentTypeValue();
			//class object is not neccessary
/*			Class<?> docClazz = null;
			try
			{
				docClazz = Class.forName(docType);
			}
			catch (ClassNotFoundException e)
			{
				throw new StateTransitionException("No definition for the class with the specified name could be found.", e);
			}*/
			final RutaProcess process = (RutaProcess) correspondence.getState();
			if(docType.contains("OrderResponseSimpleType"))
			{
				orderResponseSimple = ((BuyerOrderingProcess) process).getOrderResponseSimple();
				final boolean accepted = orderResponseSimple.getAcceptedIndicator().isValue();
				if(accepted)
					changeState(process, BuyerAcceptOrderState.getInstance());
				else
					changeState(process, BuyerOrderRejectedState.getInstance());
			}
			else if(docType.contains("OrderResponse"))
			{
				//MMM to implement decision on what to do next
				changeState(process, BuyerChangeOrderState.getInstance());
				changeState(process, BuyerCancelOrderState.getInstance());
			}
		}
		else
			throw new StateTransitionException("Document could not be found in the correspondence.");
	}
}