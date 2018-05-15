package rs.ruta.client.correspondence;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public class BuyerSendOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerSendOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
			final OrderType order = ((BuyerOrderingProcess) process).getOrder();
			process.getClient().cdrSendOrder(order);
			correspondence.addDocumentReference(correspondence.getClient().getMyParty().getCoreParty(),
					order.getUUIDValue(), order.getIDValue(), order.getIssueDateValue(),
					order.getIssueTimeValue(), order.getClass().getName(),
					correspondence.getClient().getMyParty());
			changeState(process, BuyerReceiveOrderResponseState.getInstance());
	}
}