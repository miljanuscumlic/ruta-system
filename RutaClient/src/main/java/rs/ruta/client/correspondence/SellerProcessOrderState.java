package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerProcessOrderState")
public class SellerProcessOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerProcessOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		//MMM change code below to reflect the real activity of this state
		changeState(process, SellerAcceptOrderState.getInstance());
	}
}
