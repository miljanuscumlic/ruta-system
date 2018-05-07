package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerChangeOrderState")
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
	public void doActivity(Correspondence correspondence)
	{
		//MMM to implement
		changeState((RutaProcess) correspondence.getState(), BuyerReceiveOrderResponseState.getInstance());
	}
}