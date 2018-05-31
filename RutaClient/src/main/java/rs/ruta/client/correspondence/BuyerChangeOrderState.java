package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import rs.ruta.client.MyParty;

@XmlRootElement(name = "BuyerChangeOrderState")
public class BuyerChangeOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerChangeOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final OrderChangeType orderChange = prepareOrderChange(correspondence);
		if(orderChange != null)
		{
			saveOrderChange(correspondence, orderChange);
			changeState(process, BuyerSendOrderChangeState.getInstance());
		}
		else
		{
			process.setOrderChanged(false);
			process.decreaseOrderChangeSequenceNumber();
			changeState(process, BuyerProcessOrderResponseState.getInstance());
		}
	}

	/**
	 * Prepares {@link OrderChangeType Order Change} populated with some data from the {@link OrderType Order}.
	 * @param correspondence correspondence that process of this state belongs to
	 * @return Order Change or {@code null} if its creation has failed, or has been aborted by the user
	 * or the Order Change document doesn't conform to the UBL standard
	 */
	private OrderChangeType prepareOrderChange(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final MyParty myParty = process.getClient().getMyParty();
		return myParty.produceOrderChange(process.getOrderResponse(correspondence), process.getNextOrderChangeSequenceNumber());
	}

	/**
	 * Sets Order Change field of the process, adds it's {@link DocumentReference} to the correspondence
	 * and stores it in the database.
	 * @param correspondence which Order Change is part of
	 * @param orderChange Order Change to save
	 */
	private void saveOrderChange(Correspondence correspondence, OrderChangeType orderChange)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		process.setOrderChange(orderChange);
		correspondence.addDocumentReference(orderChange.getBuyerCustomerParty().getParty(),
				orderChange.getUUIDValue(), orderChange.getIDValue(),
				orderChange.getIssueDateValue(), orderChange.getIssueTimeValue(),
				orderChange.getClass().getName(), DocumentReference.Status.UBL_VALID);
		correspondence.storeDocument(orderChange);
	}

}