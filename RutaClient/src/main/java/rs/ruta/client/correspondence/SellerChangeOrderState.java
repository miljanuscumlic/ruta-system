package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerChangeOrderState")
public class SellerChangeOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerChangeOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		changeState(process, SellerProcessOrderState.getInstance());
	}
}
