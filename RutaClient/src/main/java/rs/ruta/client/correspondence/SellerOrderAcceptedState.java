package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

//MMM this state is superfluous
@XmlRootElement(name = "SellerOrderAcceptedState")
public class SellerOrderAcceptedState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerOrderAcceptedState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		process.setOrderAccepted(true);
		changeState(process, ClosingState.getInstance());
	}
}
