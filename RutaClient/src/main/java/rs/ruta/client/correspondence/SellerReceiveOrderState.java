package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerReceiveOrderState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller")
public class SellerReceiveOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerReceiveOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		changeState(process, SellerProcessOrderState.getInstance());
	}
}
