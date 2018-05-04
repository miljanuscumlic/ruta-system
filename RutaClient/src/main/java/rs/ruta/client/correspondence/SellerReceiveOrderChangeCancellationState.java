package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerReceiveOrderChangeCancellationState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller")
public class SellerReceiveOrderChangeCancellationState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerReceiveOrderChangeCancellationState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		//MMM to implement

		//implementing timeout
		changeState(process, EndOfProcessState.getInstance());
	}
}
