package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerAcceptOrderState")
public class BuyerAcceptOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerAcceptOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveOrderResponse(final RutaProcess process)
	{
		//MMM to implement
		changeState(process, BuyerOrderAcceptedState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		//MMM to implement
		changeState((RutaProcess) correspondence.getState(), BuyerOrderAcceptedState.getInstance());
	}
}