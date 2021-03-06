package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.MyParty;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.datamapper.DetailException;

@XmlRootElement(name = "BuyerCancelOrderState")
public class BuyerCancelOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerCancelOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final OrderCancellationType orderCancellation = prepareOrderCancellation(correspondence);
		if(orderCancellation != null)
		{
			saveOrderCancellation(correspondence, orderCancellation);
			changeState(process, BuyerSendOrderCancellationState.getInstance());
		}
		else
		{
			process.setOrderCancelled(false);
			changeState(process, BuyerProcessOrderResponseState.getInstance());
		}
	}

	/**
	 * Prepares {@link OrderCancellationType Order Cancellation} populated with some data from the {@link OrderType Order}.
	 * @param correspondence correspondence that process of this state belongs to
	 * @return Order Cancellation or {@code null} if its creation has failed, or has been aborted by the user
	 * or the Order Cancellation document doesn't conform to the UBL standard
	 */
	private OrderCancellationType prepareOrderCancellation(Correspondence correspondence)
	{
		OrderCancellationType orderCancellation = null;
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final MyParty myParty = process.getClient().getMyParty();
		final DocumentReference docReference = correspondence.getLastDocumentReference();
		if(docReference != null)
		{
			if(OrderResponseType.class.getName().equals(docReference.getDocumentTypeValue()))
				orderCancellation = myParty.produceOrderCancellation(process.getOrderResponse(correspondence));
			else if(OrderResponseSimpleType.class.getName().equals(docReference.getDocumentTypeValue()))
				orderCancellation = myParty.produceOrderCancellation(process.getOrderResponseSimple(correspondence));
		}
		else
			throw new StateActivityException(Messages.getString("BuyerCancelOrderState.0")); 

		return orderCancellation;
	}

	/**
	 * Sets Order Cancellation field of the process, adds it's {@link DocumentReference} to the correspondence
	 * and stores it in the database.
	 * @param correspondence which Order Cancellation is part of
	 * @param orderCancellation Order Cancellation to save
	 */
	private void saveOrderCancellation(Correspondence correspondence, OrderCancellationType orderCancellation)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		process.setOrderCancellation(orderCancellation);
//		correspondence.addDocumentReference(orderCancellation.getBuyerCustomerParty().getParty(),
//				orderCancellation.getUUIDValue(), orderCancellation.getIDValue(),
//				orderCancellation.getIssueDateValue(), orderCancellation.getIssueTimeValue(),
//				orderCancellation.getClass().getName(), DocumentReference.Status.UBL_VALID);

		try
		{
			correspondence.addDocumentReference(orderCancellation, DocumentReference.Status.UBL_VALID);
			correspondence.storeDocument(orderCancellation);
		}
		catch (DetailException e)
		{
			throw new StateActivityException(e.getMessage());
		}


	}

}