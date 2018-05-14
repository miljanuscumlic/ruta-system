package rs.ruta.client.correspondence;

import java.awt.Color;
import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.client.gui.RutaClientFrame;

@XmlRootElement(name = "BuyerPlaceOrderState")
public class BuyerPlaceOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerPlaceOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	@Deprecated
	public void placeOrder(final RutaProcess process)
	{
		produceOrder(process, "dummy");
		sendOrder(process);
		changeState(process, BuyerReceiveOrderResponseState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		produceOrder(process, correspondence.getCorrespondentID());
		final OrderType order = ((BuyerOrderingProcess) process).getOrder();
		sendOrder(process);
		correspondence.addDocumentReference(correspondence.getClient().getMyParty().getCoreParty(),
				order.getUUIDValue(), order.getIDValue(), order.getIssueDateValue(),
				order.getIssueTimeValue(), order.getClass().getName(),
				correspondence.getClient().getMyParty());
		changeState(process, BuyerReceiveOrderResponseState.getInstance());
	}

	private void produceOrder(RutaProcess process, String correspondentID)
	{
		final RutaClient client = process.getClient();
		final RutaClientFrame clientFrame = client.getClientFrame();
		clientFrame.appendToConsole(new StringBuilder("Collecting data and producing the Order..."), Color.BLACK);
		final MyParty myParty = client.getMyParty();
		final OrderType order = myParty.produceOrder(correspondentID);
		if(order == null)
			throw new StateTransitionException("Order is malformed. UBL validation has failed.");
		((BuyerOrderingProcess) process).setOrder(order);
	}

	private void sendOrder(RutaProcess process)
	{
		final OrderType order = ((BuyerOrderingProcess) process).getOrder();
		process.getClient().cdrSendOrder(order);
	}

}