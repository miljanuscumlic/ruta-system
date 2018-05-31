package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.gui.ProcessOrderResponseDialog;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "BuyerProcessOrderResponseState")
public class BuyerProcessOrderResponseState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerProcessOrderResponseState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final DocumentReference docReference = correspondence.getLastDocumentReference();
		if(docReference != null)
		{
			final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
			try
			{
				correspondence.block();
				final OrderResponseType orderResponse = process.getOrderResponse(correspondence);
				if(orderResponse != null)
				{
					decideOnOrderResponse(process, orderResponse);
					if(process.isOrderChanged())
						changeState(process, BuyerChangeOrderState.getInstance());
					else if(process.isOrderCancelled())
						changeState(process, BuyerCancelOrderState.getInstance());
					else if(process.isOrderAccepted())
						changeState(process, BuyerAcceptOrderState.getInstance());
				}
				else
					throw new StateActivityException("Order Response could not be found in the correspondence.");
			}
			catch(InterruptedException e)
			{
				if(!correspondence.isStopped()) //non-intentional interruption
					throw new StateActivityException("Correspondence has been interrupted!");
			}
		}
		else
			throw new StateActivityException("Document could not be found in the correspondence.");
	}

	/**
	 * Displays dialog for making the decision on what kind of response should be on Order Response.
	 * @param process current process
	 * @param orderResponse Order Response to make decision on
	 */
	private void decideOnOrderResponse(BuyerOrderingProcess process, OrderResponseType orderResponse)
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final String decision = clientFrame.showProcessOrderResponseDialog(orderResponse);
		if(InstanceFactory.CHANGE_ORDER.equals(decision))
			process.setOrderChanged(true);
		else if(InstanceFactory.CANCEL_ORDER.equals(decision))
			process.setOrderCancelled(true);
		else if(InstanceFactory.ACCEPT_ORDER.equals(decision))
			process.setOrderAccepted(true);
	}
}