package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerCancelOrderState")
public class SellerCancelOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerCancelOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		process.setOrderCancelled(true);
		changeState(process, ClosingState.getInstance());
	}
}
