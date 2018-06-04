package rs.ruta.client.correspondence;

import java.awt.Color;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.client.RutaClient;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "BuyerPrepareOrderState")
public class BuyerPrepareOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerPrepareOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		final OrderType order = prepareOrder(process, correspondence.getCorrespondentID());
		if(order != null)
		{
			saveOrder(correspondence, order);
			changeState(process, BuyerSendOrderState.getInstance());
		}
		else
		{
			correspondence.setDiscarded(true);
			process.getClient().getClientFrame().appendToConsole(new StringBuilder("Order has been discarded."), Color.BLACK);
			changeState(process, ClosingState.getInstance());
		}
	}

	/**
	 * Creates {@link OrderType Order} populating it with the data.
	 * @param process process that this state belongs to
	 * @param correspondentID correspondent's ID
	 * @return prepared Order or {@code null} if Order creation has been failed or has been discarded
	 * by the user, or Order does not conform to the {@code UBL} standard
	 */
	private OrderType prepareOrder(RutaProcess process, String correspondentID)
	{
		final RutaClient client = process.getClient();
		client.getClientFrame().appendToConsole(new StringBuilder("Collecting data and preparing the Order..."), Color.BLACK);
		return client.getMyParty().produceOrder(correspondentID);
	}

	/**
	 * Sets Order in the process, adds it's {@link DocumentReference} to the correspondence and stores it
	 * in the database.
	 * @param correspondence which Order is part of
	 * @param order Order to save
	 */
	private void saveOrder(Correspondence correspondence, OrderType order)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		((BuyerOrderingProcess) process).setOrder(order);
		correspondence.addDocumentReference(order.getBuyerCustomerParty().getParty(),
				order.getUUIDValue(), order.getIDValue(), order.getIssueDateValue(),
				order.getIssueTimeValue(), order.getClass().getName(), DocumentReference.Status.UBL_VALID);
		correspondence.storeDocument(order);
	}
}