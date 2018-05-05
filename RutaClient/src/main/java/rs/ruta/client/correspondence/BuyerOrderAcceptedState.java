package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerOrderAcceptedState")
public class BuyerOrderAcceptedState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerOrderAcceptedState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveOrderResponse(final RutaProcess process)
	{
		//MMM to implement
		changeState(process, EndOfProcessState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		//MMM to implement
		changeState(process, EndOfProcessState.getInstance());
	}
}