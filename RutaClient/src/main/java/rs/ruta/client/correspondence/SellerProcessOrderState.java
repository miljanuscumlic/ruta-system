package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "SellerProcessOrderState")
public class SellerProcessOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerProcessOrderState();
	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence)
	{
		try
		{
			correspondence.block();

			final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
			final DocumentReference documentReference = correspondence.getLastDocumentReference();
			OrderType order = null;
			String dialogTitle = null;
			if(OrderType.class.getName().equals(documentReference.getDocumentTypeValue()))
			{
				order = process.getOrder(correspondence);
				dialogTitle = Messages.getString("SellerProcessOrderState.0"); 
				if(order == null)
					throw new StateActivityException(Messages.getString("SellerProcessOrderState.1")); 
			}
			else if(OrderChangeType.class.getName().equals(documentReference.getDocumentTypeValue()))
			{
				final OrderChangeType orderChange = process.getOrderChange(correspondence);
				dialogTitle = Messages.getString("SellerProcessOrderState.2"); 
				if(orderChange == null)
					throw new StateActivityException(Messages.getString("SellerProcessOrderState.3")); 
				else
				{
					order = process.getOrder(correspondence).clone();
					order.setOrderLine(orderChange.getOrderLine());
				}
			}

			if(checkCatalogueVersion(order, correspondence))
			{
				decideOnOrder(process, order, dialogTitle);
				if(process.isOrderAccepted())
					changeState(process, SellerAcceptOrderState.getInstance());
				else if(process.isOrderRejected())
					changeState(process, SellerRejectOrderState.getInstance());
				else if(process.isOrderModified())
					changeState(process, SellerAddDetailState.getInstance());
//				else // user decided to postpone the decision
//					changeState(process, SellerProcessOrderState.getInstance());
			}
			else
			{
				process.setObsoleteCatalogue(true);
				changeState(process, SellerRejectOrderState.getInstance());
			}
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped()) //non-intentional interruption
				throw new StateActivityException(Messages.getString("SellerProcessOrderState.4")); 
		}
	}

	/**
	 * Displays dialog for making the decision on what kind of response should be on Order.
	 * @param process current process
	 * @param order Order to make decision on
	 * @param dialogTitle
	 */
	private void decideOnOrder(SellerOrderingProcess process, OrderType order, String dialogTitle)
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final String decision = clientFrame.showProcessOrderDialog(dialogTitle, order);
		if(InstanceFactory.ACCEPT_ORDER.equals(decision))
			process.setOrderAccepted(true);
		else if(InstanceFactory.REJECT_ORDER.equals(decision))
			process.setOrderRejected(true);
		else if(InstanceFactory.ADD_DETAIL.equals(decision))
			process.setOrderModified(true);
	}

	/**
	 * Checks whether received Order has a reference to the current i.e. most recently uploaded
	 * Seller's Catalogue.
	 * @param order Order to check
	 * @param correspondence current correspondence
	 * @return true if the reference is to the current catalogue; false otherwise or some exception
	 * is thrown
	 */
	private boolean checkCatalogueVersion(OrderType order, Correspondence correspondence)
	{
		boolean currentCatalogue = true;
		try
		{
			if(! order.getCatalogueReference().getIDValue().
					equals(correspondence.getClient().getMyParty().getMyFollowingParty().getCatalogue().getIDValue()))
			{
				final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
				process.setOrderRejected(true);
				process.setOrderAccepted(false);
				currentCatalogue = false;
			}
		}
		catch(Exception e)
		{
			currentCatalogue = false;
		}
		return currentCatalogue;
	}
}