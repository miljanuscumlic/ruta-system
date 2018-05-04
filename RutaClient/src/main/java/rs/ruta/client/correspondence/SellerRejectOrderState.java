package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerRejectOrderState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller")
public class SellerRejectOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerRejectOrderState();

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
