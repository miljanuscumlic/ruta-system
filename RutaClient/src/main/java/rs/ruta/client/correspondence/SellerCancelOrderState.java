package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerCancelOrderState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller")
public class SellerCancelOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerCancelOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		//MMM to implement
		//changeState(process, BuyerOrderAcceptedState.getInstance());
	}
}
