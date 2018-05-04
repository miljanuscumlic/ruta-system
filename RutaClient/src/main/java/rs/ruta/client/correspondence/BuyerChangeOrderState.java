package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerChangeOrderState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer")
public class BuyerChangeOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerChangeOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveOrderResponse(final RutaProcess process)
	{
		//MMM to implement
		changeState(process, BuyerReceiveOrderResponseState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		//MMM to implement
		changeState(process, BuyerReceiveOrderResponseState.getInstance());
	}
}