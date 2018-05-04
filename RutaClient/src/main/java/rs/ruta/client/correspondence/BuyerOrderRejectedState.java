package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerOrderRejectedState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer")
public class BuyerOrderRejectedState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerOrderRejectedState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void orderRejected(final RutaProcess process)
	{
		//MMM to implement
		changeState(process, EndOfProcessState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		changeState(process, EndOfProcessState.getInstance());
	}
}