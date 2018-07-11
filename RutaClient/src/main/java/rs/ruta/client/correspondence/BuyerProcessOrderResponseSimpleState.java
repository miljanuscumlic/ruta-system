package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AcceptedIndicatorType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.gui.ProcessOrderResponseSimpleDialog;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "BuyerProcessOrderResponseSimpleState")
public class BuyerProcessOrderResponseSimpleState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerProcessOrderResponseSimpleState();

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
				final OrderResponseSimpleType orderResponseSimple = process.getOrderResponseSimple(correspondence);
				if(orderResponseSimple != null)
				{
					final AcceptedIndicatorType acceptedIndicator = orderResponseSimple.getAcceptedIndicator();
					if(acceptedIndicator != null)
					{
						if(acceptedIndicator.isValue())
						{
							decideOnOrderResponseSimple(process, orderResponseSimple);
							if(process.isOrderAccepted())
								changeState(process, BuyerAcceptOrderState.getInstance());
							else if(process.isOrderCancelled())
								changeState(process, BuyerCancelOrderState.getInstance());
						}
						else
						{
							process.getClient().getClientFrame().showOrderResponseSimpleDialog(Messages.getString("BuyerProcessOrderResponseSimpleState.0"), 
									orderResponseSimple, false, false, null);
							changeState(process, BuyerOrderRejectedState.getInstance());
						}
					}
					else
						throw new StateActivityException(Messages.getString("BuyerProcessOrderResponseSimpleState.1")); 
				}
				else
					throw new StateActivityException(Messages.getString("BuyerProcessOrderResponseSimpleState.2")); 
			}
			catch(InterruptedException e)
			{
				if(!correspondence.isStopped()) //non-intentional interruption
					throw new StateActivityException(Messages.getString("BuyerProcessOrderResponseSimpleState.3")); 
			}
		}
		else
			throw new StateActivityException(Messages.getString("BuyerProcessOrderResponseSimpleState.4")); 
	}

	/**
	 * Displays dialog for making the decision on what kind of response should be on Order Response Simple.
	 * @param process current process
	 * @param orderResponse Order Response Simple to make decision on
	 */
	private void decideOnOrderResponseSimple(BuyerOrderingProcess process, OrderResponseSimpleType orderResponseSimple)
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final String decision = clientFrame.showProcessOrderResponseSimpleDialog(orderResponseSimple);
		if(InstanceFactory.ACCEPT.equals(decision))
			process.setOrderAccepted(true);
		else if(InstanceFactory.CANCEL_ORDER.equals(decision))
			process.setOrderCancelled(true);
	}

}